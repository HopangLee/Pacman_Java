import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;


public class Ghost extends GameObject{
	private BufferedImage[] ghostSprite;
	/* 
	 * ���� �̹��� index 
	 * (0: ������, 1: ����, 2: ����, 3: �Ʒ���, frightened : 4, Consumed: 5)
	 */
	private int imageIndex = 0; 
	
	public float ghostReleaseTimer = 0; // ������ ������ �ð� -> ������ ���� ����
	public int pinkyReleaseTimer = 5; 
	
	//public boolean isInGhostHouse = true; // ������ ���� ���� �ִ���
	
	/* 
	 * ���̵����� Ž�� �ð��� �߰� �ð�
	 * scatter : Ž��, chase : �߰�
	 * �ð��� ������ Ž���ð��� ª������ �߰ݽð��� �þ 
	 */
	private float modeChangeTimer = 0; // ��尡 �ٲ� �ð��� �� -> �ʿ�� ��ȯ
	private float frightenedModeTimer = 0; //frightenMode�� ������ �ð��� �� -> �ʿ�� ��ȯ
	public int frightenedModeDuration = 10; // frigtenedMode�� ������ �ð�
	
	//private float blinkTimer = 0;
	//public int startBlinkingAt = 7; // frightened ��尡 ���� �������� ǥ���ϱ� �����ϴ� �ð�(���ص���)
	 
	//private boolean frightenedModelsWhite = false; //�Ƹ� ��� ���� ��
	
	public float scatterModeTimer1 = 4;
	public float chaseModeTimer1 = 4;
	public float scatterModeTimer2 = 9;
	public float chaseModeTimer2 = 8;
	public float scatterModeTimer3 = 8;
	public float chaseModeTimer3 = 9;
	public float scatterModeTimer4 = 7;
	public float chaseModeTimer4 = 10;
	public float scatterModeTimer5 = 6;
	public float chaseModeTimer5 = 11;
	
	Node ghostHouse; // �� ��� ��
	Node currentPosition;
	Pacman pacMan;
	Node currentNode, previousNode, targetNode, homeNode;
	int movingSpeed = 2;
	int frightenedMovingSpeed = 1;
	int previousMovingSpeed;
	int consumedMoveSpeed = 3;
	int normalMoveSpeed = 2;
	
	GameBoard board;
	Vector2 direction, nextDirection;
	
	boolean rightDir;
	
	boolean isActive = false; // ���� ������ Ȱ��ȭ�� �����
	
	public Mode currentMode = Mode.Scatter; // ������ ���� ���
	Mode previousMode;
	private int modeChangeIteration = 1;
	
	public GhostType ghostType = GhostType.Red;
	
	public int[] nodeX = new int[510];
	public int[] nodeY = new int[510];
	public int nodeCount = 0;
	
	private int boardWidth = 21;
	private int boardHeight = 24;
	public float[][] minDistance = new float[boardWidth][boardHeight];
	
	private int scale = GameBoard.SCALE;
	
	public enum Mode{
		Scatter,
		Chase,
		Consumed,
		frighted
	}
	
	public enum GhostType{
		Red,
		Pink,
		Blue,
		Orange
	}
	
	public Ghost(Node currentNode, Pacman pacMan, GameBoard board, GhostType ghostType) {
		this.currentNode = currentNode; // Node[31]
		this.pacMan = pacMan;
		this.board = board;
		this.ghostType = ghostType;
		
		ghostSprite = new BufferedImage[6];
		try {
			switch(ghostType) {
			case Red:				
				ghostSprite[0] = ImageIO.read(getClass().getResource("/ghost_red.png"));
				ghostSprite[1] = ImageIO.read(getClass().getResource("/ghost_red.png"));
				ghostSprite[2] = ImageIO.read(getClass().getResource("/ghost_red.png"));
				ghostSprite[3] = ImageIO.read(getClass().getResource("/ghost_red.png"));
				System.out.println("red");
				break;
				
			case Blue:				
				ghostSprite[0] = ImageIO.read(getClass().getResource("/ghost_blue.png"));
				ghostSprite[1] = ImageIO.read(getClass().getResource("/ghost_blue.png"));
				ghostSprite[2] = ImageIO.read(getClass().getResource("/ghost_blue.png"));
				ghostSprite[3] = ImageIO.read(getClass().getResource("/ghost_blue.png"));
				System.out.println("blue");
				break;
				
			case Orange:				
				ghostSprite[0] = ImageIO.read(getClass().getResource("/ghost_orange.png"));
				ghostSprite[1] = ImageIO.read(getClass().getResource("/ghost_orange.png"));
				ghostSprite[2] = ImageIO.read(getClass().getResource("/ghost_orange.png"));
				ghostSprite[3] = ImageIO.read(getClass().getResource("/ghost_orange.png"));
				System.out.println("orange");
				break;
				
			case Pink:				
				ghostSprite[0] = ImageIO.read(getClass().getResource("/ghost_pink.png"));
				ghostSprite[1] = ImageIO.read(getClass().getResource("/ghost_pink.png"));
				ghostSprite[2] = ImageIO.read(getClass().getResource("/ghost_pink.png"));
				ghostSprite[3] = ImageIO.read(getClass().getResource("/ghost_pink.png"));
				System.out.println("pink");
				break;
			}
			
			ghostSprite[4] = ImageIO.read(getClass().getResource("/ghost_frighten.png"));
			ghostSprite[5] = ImageIO.read(getClass().getResource("/ghost_consumed.png"));
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void start() {
		switch(ghostType) {
		case Red:
			//System.out.println("red");
			homeNode = board.nodes[71];
			break;
			
		case Blue:
			//System.out.println("blue");
			homeNode = board.nodes[72];
			break;
			
		case Orange:
			//System.out.println("orange");
			homeNode = board.nodes[73];
			break;
			
		case Pink:
			//System.out.println("pink");
			homeNode = board.nodes[70];
			break;
		}
		
		ghostHouse = board.nodes[31];
		
		x = currentNode.x;
		y = currentNode.y;
		
		setBounds(x, y, 27, 27);
		
		nodeX = board.nodeX;
		nodeY = board.nodeY;
		nodeCount = board.nodes.length;
		for(int i = 0; i < nodeCount; i++) {
			SetDijkstra(nodeX[i], nodeY[i]);
		}
		
		// ���� ���� ������ �ʱ� ���� ���� ����
		direction = Vector2.Up;
		targetNode = currentNode.neighbors[0];
		
		/*
		if(isInGhostHouse) {
			direction = Vector2.Up;
			targetNode = currentNode.neighbors[0];
		}
		else {
			// ���� ���� ���� �� �ʱ� ���� ����
			direction = Vector2.Right;
			targetNode = ChooseNextNode();
		}*/
		
		previousNode = currentNode;
	}
	
	@Override
	public void update() {
		ModeUpdate();
		Move();
		CheckIsInGhostHouse();
		//setLocation(x, y);
	}
	
	void SetDijkstra(int x, int y) {
		float[][] distance = new float[boardWidth][boardHeight];
		int[][] check = new int[boardWidth][boardHeight];
		int checkN = 0;
		
		for(int i = 0; i < boardWidth; i++) {
			for(int j = 0; j < boardHeight; j++) {
				distance[i][j] = -1;
			}
		}
		for(int i = 0; i < nodeCount; i++) {
			distance[nodeX[i]][nodeY[i]] = 9999f;
		}
		distance[x][y] = 0;
		while(true) {
			if(nodeCount + 1 == checkN)
				break;
			float min = 10000;
			int visitX = 0, visitY = 0;
			for(int i = 0; i < boardWidth; i++) {
				for(int j = 0; j < boardHeight; j++) {
					if(min > distance[i][j] && distance[i][j] != -1 && check[i][j] == 0) {
						min = distance[i][j];
						visitX = i;
						visitY = j;
					}
				}
			}
			
			Node visitNode = board.board[visitX][visitY];
			for(int i = 0; i < visitNode.neighbors.length; i++) {
				if(distance[visitNode.neighbors[i].x/scale][visitNode.neighbors[i].y/scale] > distance[visitX][visitY] + GetDistance(visitNode, visitNode.neighbors[i])) {
					distance[visitNode.neighbors[i].x/scale][visitNode.neighbors[i].y/scale] = distance[visitX][visitY] + GetDistance(visitNode, visitNode.neighbors[i]);
				}
			}
			check[visitX][visitY] = 1;
			checkN++;
		}
		minDistance[x][y] = distance[ghostHouse.x/scale][ghostHouse.y/scale];
	}
	
	float GetDistance(GameObject a, GameObject b) {		
		float dx = a.x - b.x;
		float dy = a.y - b.y;
		float dis = dx * dx + dy * dy;
		return (float) Math.sqrt(dis);
	}

	Node GetTargetTile() {
		Node targetTile= null;
		if(ghostType==GhostType.Red)
        {
            targetTile = GetRedGhostTargetTile();
        }else if (ghostType == GhostType.Pink)
        {
            targetTile = GetPinkyGhostTargetTile();
        }else  if(ghostType== GhostType.Blue)
        {
            targetTile = GetBlueGhostTargetTile();
        }
        else if(ghostType== GhostType.Orange)
        {
            targetTile = GetOrangeGhostTargetTile();
        }
		
		return targetTile;
	}
	
	Node ChooseNextNode() {
		// ���� ���� ��忡 ���� �Ѿư��� Ȩ���� ���� ����
		Node targetTile = null; // -> �ٲ�
		if(currentMode == Mode.Chase) {
			targetTile = GetTargetTile();
		}
		else if(currentMode == Mode.Scatter) {
			targetTile = homeNode;
		}
		else if(currentMode == Mode.frighted) {
			targetTile = GetRandomTile();
		}
		else if(currentMode == Mode.Consumed) {
			targetTile = ghostHouse;
		}
		
		Node moveToNode = null;
		Node[] foundNodes = new Node[4];
		Vector2[] foundNodesDirection = new Vector2[4];
		
		int nodeCounter = 0, ch = 0, inverseDirection = 0;
		
		if(currentMode == Mode.Consumed) {
			for(int i = 0; i < currentNode.neighbors.length; i++) {
				foundNodes[nodeCounter] = currentNode.neighbors[i];
				foundNodesDirection[nodeCounter] = currentNode.validDirection[i];
				nodeCounter++;
				ch = 1;
			}
		}
		else {
			for(int i = 0; i < currentNode.neighbors.length; i++) {
				Vector2 minusDirection = Vector2.Right; // �Ѹ��� ���� ���� ������ �ݴ����
				switch(direction) {
				case Up:
					minusDirection = Vector2.Down;
					break;
					
				case Down:
					minusDirection = Vector2.Up;
					break;
					
				case Left:
					minusDirection = Vector2.Right;
					break;
					
				case Right:
					minusDirection = Vector2.Left;
					break;
				case Zero:
					minusDirection = Vector2.Zero;
					break;
				}
				if(currentNode.validDirection[i] != minusDirection) {
					foundNodes[nodeCounter] = currentNode.neighbors[i];
					foundNodesDirection[nodeCounter] = currentNode.validDirection[i];
					nodeCounter++;
					ch = 1;
				}
				else if(currentNode.validDirection[i] == minusDirection) {
					inverseDirection = i;
				}
			}
			

		}
		if(ch==0) {
			foundNodes[nodeCounter] = currentNode.neighbors[inverseDirection];
			foundNodesDirection[nodeCounter] = currentNode.validDirection[inverseDirection];
			nodeCounter++;
		}
		if(nodeCounter == 1) {
			moveToNode = foundNodes[0];
			direction = foundNodesDirection[0];
		}
		else if(nodeCounter > 1) {
			if(currentMode == Mode.Consumed) {
				float distance = 999999f;
				for (int i = 0; i < foundNodes.length; i++) {
					if(foundNodes[i] != null) {
						if(minDistance[foundNodes[i].x/scale][foundNodes[i].y/scale] + GetDistance(this, foundNodes[i]) < distance) {
							moveToNode = foundNodes[i];
							direction = foundNodesDirection[i];
							distance = minDistance[foundNodes[i].x/scale][foundNodes[i].y/scale] + GetDistance(this, foundNodes[i]);
						}
					}
				}
			}
			else {
				float leastDistance = 10000f;
				for(int i = 0; i < foundNodes.length; i++) {
					if(foundNodes[i] != null) {
						
						float distance = GetDistance(foundNodes[i], targetTile);
						
						/*
						if(ghostType==GhostType.Red)
				        {
				            System.out.print("R: ");
				        }else if (ghostType == GhostType.Pink)
				        {
				        	System.out.print("P: ");
				        }else  if(ghostType== GhostType.Blue)
				        {
				        	System.out.print("B: ");
				        }
				        else if(ghostType== GhostType.Orange)
				        {
				        	System.out.print("O: ");
				        }
						System.out.println("foundNodes["+i+"]("+ foundNodes[i].x +", "+ foundNodes[i].y +"), " + "targetTile("+ targetTile.x +", "+ targetTile.y +")");
						System.out.println("distance: " + distance);*/
						
						if(distance < leastDistance) {
							leastDistance = distance;
							moveToNode = foundNodes[i];
							direction = foundNodesDirection[i];
						}
					}
				}
			}
		}
		return moveToNode;
	}
	
	Node GetRedGhostTargetTile()
    {
        // �Ѹ��� �������� �˰� �Ѹ��� ���� ��带 �ٷ� ���󰡴� AI
		Node targetTile = new Node(pacMan.x, pacMan.y);
		
        return targetTile;
    }
    Node GetPinkyGhostTargetTile()
    {
        //�Ѹ��� ���� �������� �Ѹ��� Ÿ�ٳ��� ���� AI
    	Vector2 pacManOrientation = pacMan.direction;
    	int targetx = 0, targety = 0;
    	switch(pacManOrientation) {
    	case Up:
    		targety = 4;
    		break;
    		
    	case Down:
    		targety = -4;
    		break;
    		
    	case Left:
    		targetx = -4;
    		break;
    		
    	case Right:
    		targetx = 4;
    		break;
    		
    	case Zero:
    		break;
    	}
    	
    	Node targetTile = new Node(pacMan.x + targetx, pacMan.y + targety);
		
        return targetTile;
    }
    Node GetBlueGhostTargetTile()
    {
        // �Ѹ��� ���� �������� �Ѹ��� 4Ÿ�� ���� ������ ���� AI
    	Vector2 pacManOrientation = pacMan.direction;
    	int targetx = 0, targety = 0;
    	switch(pacManOrientation) {
    	case Up:
    		targety = -4;
    		break;
    		
    	case Down:
    		targety = +4;
    		break;
    		
    	case Left:
    		targetx = +4;
    		break;
    		
    	case Right:
    		targetx = -4;
    		break;
    		
    	case Zero:
    		break;
    	}
    	
    	Node targetTile = new Node(pacMan.x + targetx, pacMan.y + targety);
		
        return targetTile;
    }
    Node GetOrangeGhostTargetTile()
    {
    	Vector2 pacManOrientation = pacMan.direction;
    	int targetx = 0, targety = 0;
    	switch(pacManOrientation) {
    	case Up:
    		targety = 4;
    		break;
    		
    	case Down:
    		targety = -4;
    		break;
    		
    	case Left:
    		targetx = -4;
    		break;
    		
    	case Right:
    		targetx = 4;
    		break;
    		
    	case Zero:
    		break;
    	}
    	
    	Node targetTile = new Node(x + targetx, y + targety);
		
        return targetTile;
    }
    
    Node GetRandomTile() {
    	Random rand = new Random();
    	int targetx = rand.nextInt(boardWidth);
		int targety = rand.nextInt(boardHeight);
		
		Node targetTile = new Node(targetx, targety);
		
		return targetTile;
    }
    
    void ModeUpdate() {
    	if(currentMode != Mode.frighted) {
    		// modeChangeTimer  += Time.deltaTime;
    		if(modeChangeIteration == 1) {
    			if(currentMode == Mode.Scatter && modeChangeTimer > scatterModeTimer1) {
    				ChangeMode(Mode.Chase);
    				modeChangeTimer = 0;
    			}
    			if(currentMode == Mode.Chase && modeChangeTimer > chaseModeTimer1) {
    				ChangeMode(Mode.Scatter);
    				modeChangeTimer = 0;
    				modeChangeIteration = 2;
    			}
    		}
    		else if(modeChangeIteration == 2) {
    			if(currentMode == Mode.Scatter && modeChangeTimer > scatterModeTimer2) {
    				ChangeMode(Mode.Chase);
    				modeChangeTimer = 0;
    			}
    			if(currentMode == Mode.Chase && modeChangeTimer > chaseModeTimer2) {
    				ChangeMode(Mode.Scatter);
    				modeChangeTimer = 0;
    				modeChangeIteration = 3;
    			}
    		}
    		else if(modeChangeIteration == 3) {
    			if(currentMode == Mode.Scatter && modeChangeTimer > scatterModeTimer3) {
    				ChangeMode(Mode.Chase);
    				modeChangeTimer = 0;
    			}
    			if(currentMode == Mode.Chase && modeChangeTimer > chaseModeTimer3) {
    				ChangeMode(Mode.Scatter);
    				modeChangeTimer = 0;
    				modeChangeIteration = 4;
    			}
    		}
    		else if(modeChangeIteration == 4) {
    			if(currentMode == Mode.Scatter && modeChangeTimer > scatterModeTimer4) {
    				ChangeMode(Mode.Chase);
    				modeChangeTimer = 0;
    			}
    			if(currentMode == Mode.Chase && modeChangeTimer > chaseModeTimer4) {
    				ChangeMode(Mode.Scatter);
    				modeChangeTimer = 0;
    				modeChangeIteration = 5;
    			}
    		}
    		else if(modeChangeIteration == 5) {
    			if(currentMode == Mode.Scatter && modeChangeTimer > scatterModeTimer5) {
    				ChangeMode(Mode.Chase);
    				modeChangeTimer = 0;
    			}
    			if(currentMode == Mode.Chase && modeChangeTimer > chaseModeTimer5) {
    				ChangeMode(Mode.Scatter);
    				modeChangeTimer = 0;
    				modeChangeIteration = 5;
    			}
    		}
    	}
    	else if (currentMode == Mode.frighted) {
    		//frightendModeTimer += Time.deltaTime;
    		
    		if(frightenedModeTimer >= frightenedModeDuration) {
    			frightenedModeTimer = 0;
    			ChangeMode(previousMode);
    		}
    		
    		/* �Ƹ� �����ϱ� �ð����� �� ����
    		// frightenedMode�� ���� ��������
    		if(frightenedModeTimer >= startBlinkingAt) {
    			//blinkTimer += Time.deltaTime;
    			if(blinkTimer >= 0.1f) {
    				blinkTimer = 0f;
    				
    				// �����Ÿ��� ǥ��
    				if(frightenedModelsWhite) {
    					// ��������ٰ�
    					frightenedModelsWhite = false;
    				}
    				else {
    					// �Ͼ����ٰ�
    					frightenedModelsWhite = true;
    				}
    			}
    		}*/
    	}
    }
    
    void ChangeMode(Mode m) {
    	if(currentMode == Mode.frighted) {
    		movingSpeed = previousMovingSpeed;
    	}
    	if(m == Mode.frighted) {
    		previousMovingSpeed = movingSpeed;
    		movingSpeed = frightenedMovingSpeed;
    		
    		imageIndex = 4;
    	}
    	if(currentMode != m) {
    		previousMode = currentMode;
    		currentMode = m;
    	}
    	
    	// ��������Ʈ ��Ʈ�ѷ�
    }
    
    void Move() {
    	if(currentNode != targetNode && targetNode != null /*&& !isInGhostHouse*/) {
    		if(OverShotTarget()) {
    			currentNode = targetNode;
    			x = currentNode.x;
    			y = currentNode.y;
    			
    			if(currentNode.otherNode != null) {
    				currentNode = currentNode.otherNode;
    				x = currentNode.x;
    				y = currentNode.y;
    			}
    			targetNode = ChooseNextNode();
    			previousNode = currentNode;
    			currentNode = null;
    			
    		}
    		else {
    			switch(direction) {
    			case Up:
    				if(currentMode!=Mode.frighted && currentMode != Mode.Consumed) imageIndex = 1;
    				y -= movingSpeed;
    				break;
    				
    			case Down:
    				if(currentMode!=Mode.frighted && currentMode != Mode.Consumed) imageIndex = 3;
    				y += movingSpeed;
    				break;
    				
    			case Left:
    				if(currentMode!=Mode.frighted && currentMode != Mode.Consumed) imageIndex = 2;
    				x -= movingSpeed;
    				break;
    				
    			case Right:
    				if(currentMode!=Mode.frighted && currentMode != Mode.Consumed) imageIndex = 0;
    				x += movingSpeed;
    				break;
    				
    			case Zero:
    				break;
    			}
    		}
    	}
    }
    
    boolean OverShotTarget() {
    	float nodeToTarget = LengthFromNode(targetNode.x, targetNode.y); // ���� ������ Ÿ�ٳ������� ����
		float nodeToSelf = LengthFromNode(x, y); // ���� ������ ���� ��ġ������ ����
		return nodeToSelf > nodeToTarget; // ���� ��ġ�� Ÿ�� ��� ��ġ�� �Ѿ����� true �ƴϸ� false
    }
    
    float LengthFromNode(int targetx, int targety) {
    	float fx = (float)targetx;
		float fy = (float)targety;
		
		// ����: x^2 + y^2�� ��ȯ
		return (fx - (float)previousNode.x) * (fx - (float)previousNode.x) + (fy - (float)previousNode.y) * (fy - (float)previousNode.y);
    }
    
    void CheckIsInGhostHouse() {
    	if(currentMode == Mode.Consumed) {
    		if(currentNode == ghostHouse) { // �����尡 �����(�� �߾�)��� ���� ����
    			movingSpeed = normalMoveSpeed;
    			direction = Vector2.Up;
    			targetNode = ChooseNextNode();
    			previousNode = currentNode;
    			currentMode = Mode.Chase;
    			System.out.println("�� ����!");
    			// ��������Ʈ ����
    		}
    	}
    }
    
    // �Ѹǰ� �浹���� �� ����(consumed���·� ����) (������ ���������� ���)
    public void Consumed() {
    	imageIndex = 5;
    	
    	currentMode = Mode.Consumed;
    	previousMovingSpeed = movingSpeed;
    	movingSpeed = consumedMoveSpeed;
    	
    	Node temp;
    	
    	if(currentNode == null && !(x == previousNode.x && y == previousNode.y)) {
    		if(minDistance[previousNode.x / scale][previousNode.y / scale] + GetDistance(this, previousNode) < minDistance[targetNode.x / scale][targetNode.y / scale] + GetDistance(this, targetNode)) {
    			if(x < previousNode.x) { 
    				// ghost.......previousNode
    				direction = Vector2.Right;
    			}
    			else if(x > previousNode.x) { 
    				// previousNode.......ghost
    				direction = Vector2.Left;
    			}
    			else if(y < previousNode.y) {
    				/*
    				 * previousNode
    				 * .
    				 * .
    				 * .
    				 * ghost
    				 */
    				direction = Vector2.Up;
    			}
    			else if(y > previousNode.y){
    				/*
    				 * ghost
    				 * .
    				 * .
    				 * .
    				 * previousNode
    				 */
    				direction = Vector2.Down;
    			}
    			
    			// targetNode�� previousNode�� swap
    			temp = targetNode;
    			targetNode = previousNode;
    			previousNode = temp;
    		}
    	}
    	
    	// consumed ��������Ʈ�� ����
    }
        
    // frightenedMode ����
    public void StartFrightenedMode() {
    	if(currentMode != Mode.Consumed) {
    		frightenedModeTimer = 0;
    		ChangeMode(Mode.frighted);
    	}
    }
    
    
    // �׸��� �Լ�
    @Override
    public void render(Graphics g) {
		g.drawImage(ghostSprite[imageIndex], x, y, null);
		//g.drawImage(ghostSprite[imageIndex], 200, 200, null);
	}
}
