
public class Ghost extends GameObject{
	
	public float ghostRleaseTimer = 0; // ������ ������ �ð�
	public int pinkyReleaseTimer = 5; 
	
	public boolean isInGhostHouse = false; // ������ ���� ���� �ִ���
	
	/* ���̵����� Ž�� �ð��� �߰� �ð�
	 * scatter : Ž��, chase : �߰�
	 * �ð��� ������ Ž���ð��� ª������ �߰ݽð��� �þ 
	 */
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
	int previousMovingSpeed;
	int consumedMoveSpeed = 5;
	int normalMoveSpeed = 3;
	
	GameBoard board;
	Vector2 direction, nextDirection;
	
	boolean rightDir;
	
	boolean isActive = false; // ���� ������ Ȱ��ȭ�� �����
	
	public Mode currentMode = Mode.Scatter; // ������ ���� ���
	public GhostType ghostType = GhostType.Red;
	public int[] nodeX = new int[510];
	public int[] nodeY = new int[510];
	public int nodeCount = 0;
	
	private int boardWidth = 21;
	private int boardHeight = 24;
	public float[][] minDistance = new float[boardWidth][boardHeight];
	
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
	}
	
	@Override
	public void start() {
		if(ghostType==GhostType.Red)
        {
            homeNode = board.nodes[71];
        }else if (ghostType == GhostType.Pink)
        {
        	homeNode = board.nodes[70];
        }else  if(ghostType== GhostType.Blue)
        {
        	homeNode = board.nodes[72];
        }
        else if(ghostType== GhostType.Orange)
        {
        	homeNode = board.nodes[73];
        }
		
		nodeX = board.nodeX;
		nodeY = board.nodeY;
		nodeCount = board.nodes.length;
		for(int i = 0; i < nodeCount; i++) {
			SetDijkstra(nodeX[i], nodeY[i]);
		}
		
		if(isInGhostHouse) {
			// ���� ���� ������ �ʱ� ���� ���� ����
			direction = Vector2.Up;
			targetNode = currentNode.neighbors[0];
		}
		else {
			// ���� ���� ���� �� �ʱ� ���� ����
			direction = Vector2.Right;
			targetNode = ChooseNextNode();
		}
		previousNode = currentNode;
	}
	
	@Override
	public void update() {
		
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
			distance[nodeX[i]][nodeY[i]] = 9999;
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
				if(distance[visitNode.neighbors[i].x][visitNode.neighbors[i].y] > distance[visitX][visitY] + GetDistance(visitNode, visitNode.neighbors[i])) {
					distance[visitNode.neighbors[i].x][visitNode.neighbors[i].y] = distance[visitX][visitY] + GetDistance(visitNode, visitNode.neighbors[i]);
				}
			}
			check[visitX][visitY] = 1;
			checkN++;
		}
		minDistance[x][y] = distance[ghostHouse.x][ghostHouse.y];
	}
	
	float GetDistance(GameObject a, GameObject b) {
		float dx = a.x - b.y;
		float dy = a.y - b.y;
		float dis = dx * dx + dy * dy;
		return dis;
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
			//targetTile = GetRandomTile();
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
				float distance = 999999;
				for (int i = 0; i < foundNodes.length; i++) {
					if(foundNodesDirection[i] != Vector2.Zero) {
						if(minDistance[foundNodes[i].x][foundNodes[i].y] + GetDistance(this, foundNodes[i]) < distance) {
							moveToNode = foundNodes[i];
							direction = foundNodesDirection[i];
							distance = minDistance[foundNodes[i].x][foundNodes[i].y] + GetDistance(this, foundNodes[i]);
						}
					}
				}
			}
			else {
				float leastDistance = 10000;
				for(int i = 0; i < foundNodes.length; i++) {
					if(foundNodesDirection[i] != Vector2.Zero) {
						float distance = GetDistance(foundNodes[i], targetTile);
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
        return pacMan.previousNode;
    }
    Node GetPinkyGhostTargetTile()
    {
        //�Ѹ��� ���� �������� �Ѹ��� Ÿ�ٳ��� ���� AI
        return pacMan.targetNode;
    }
    Node GetBlueGhostTargetTile()
    {
        // �Ѹ��� ���� �������� �Ѹ��� 4Ÿ�� ���� ������ ���� AI
    	return pacMan.previousNode;
    }
    Node GetOrangeGhostTargetTile()
    {
    	return pacMan.targetNode;
    }
}
