import java.awt.Graphics;

public class Pacman extends GameObject{
	
	public int score = 0; // ���߿� gameŬ�󽺳� gameboardŬ���� score�� ��ü
	
	public Vector2 direction; // �Ѹ��� �̵� ���� 
	public boolean ghostAte = false; // �Ѹ��� ���ɵ��� ��Ƹ��� �� �ִ� ��������
	public Node currentNode, previousNode, targetNode; // �Ѹ��� ���� ��ġ�ϴ� ���, ������ ��ġ�ߴ� ���, ������ ���
	
	private int speed = 4; // �Ѹ� �̵��ӵ�
	private int imageIndex = 0; // �Ѹ� �̹��� index (0: ������, 1: �Ʒ���, 2: ����, 3: ����)
	private int animation_index = 0; // �Ѹ� �ִϸ��̼� index
	private Vector2 nextDirection; // ����Ű�� ������ �Ѹ��� ������ ����
	
	private GameBoard board;
	
	public Pacman(GameBoard board) {
		this.board = board; // ��� ��带 ��� �ִ� ���Ӻ��带 ����
	}
	
	@Override
	public void start() {
		currentNode = board.nodes[69];
		
		direction = Vector2.Right;
		changePosition(direction);
	}
	
	@Override
	public void update() {
		//CheckInput(); // ����ڰ� � Ű�� �Է��ߴ��� �� �����Ӹ��� üũ
		Move(); // �Ѹ��� ��� ������
		ConsumeCoin();
		UpdateOrientation(); // �Ѹ��� �ٶ󺸴� ������ �̹����� �ٲ���
		//CheckAte();
		
		/*********************************�浹 ����************************************/
		// Coin�� �浹�� Coin�����
		for(int i = 0; i < board.coins.size(); i++) {
			if(this.intersects(board.coins.get(i))) {
				score += 10;
				board.coins.remove(i);
				break;
			}
		}
		
		// BigCoin�� �浹�� BigCoin������� ���ÿ� ������ �������·� ��ȯ
		for(int i = 0; i < board.bigCoins.size(); i++) {
			if(this.intersects(board.bigCoins.get(i))) {
				score += 50;
				board.bigCoins.remove(i);
				
				board.ghosts.get(0).StartFrightenedMode();
				board.ghosts.get(1).StartFrightenedMode();
				board.ghosts.get(2).StartFrightenedMode();
				board.ghosts.get(3).StartFrightenedMode();
				
				break;
			}
		}
		
		// ��� coin�� �� ���� -> Game Clear
		if(board.coins.size() == 0) {
			// GameClear
			System.out.println("Game Clear!");
			return;
		}
		
		for(int i = 0; i < board.ghosts.size(); i++) {
			Ghost temp = board.ghosts.get(i);
			if(this.intersects(temp)) {
				if(temp.currentMode != Ghost.Mode.Consumed) { // �̹� ���� ������ �ƴϰ�
					 if(temp.currentMode == Ghost.Mode.frighted) { // �̿� ���� �����̶�� ������ ����
						 score += 200;
						 temp.Consumed();
					 }
					 else { // �ƴ϶�� �Ѹ��� ����
						 // Game Over (��� 3��?)
						 System.out.println("Game Over!");
						 return;
					 }
				}
			}
		}
	}

	Node GetNodePosition() {
		
		return null;
	}
	
	// ������ ���� ����
	public void setAteMode() {
		ghostAte = true;
	}
	
	// �ٽ� �������� ����
	public void backtoBasicMode() {
		ghostAte = false;
	}
	
	// d�������� �Ѹ��� ������ �ٲ�(������ ��)
	void changePosition(Vector2 d) {
		if(direction != d) { // �Ѹ��� ���� �ִ� ����� �ٸ� �������� üũ
			nextDirection = d; // �Ѹ��� �� ������ d
		}
		if(currentNode != null) {
			Node moveToNode = CanMove(d); // d �������� �� �� �ִٸ� moveToNode�� �� �� �ִ� ��尡 ����
			
			if(moveToNode != null) {
				direction = d; // �Ѹ��� ������ d �������� �ٲ�
				targetNode = moveToNode; // ��ǥ��� ����
				previousNode = currentNode; // ���� �ִ� ��带 ���� ���� ����
				currentNode = null; // ���� ��� ���
			}
		}
	}
	
	// d���⿡ �� �� �ִ� ��带 ����
	Node CanMove(Vector2 d) {
		Node moveToNode = null; // �Ѹ��� ������ �ϴ� ��� ������ ���� �ʱ�ȭ
		
		for(int i = 0; i < currentNode.length; i++) { // ���� ��忡�� �̿��� ��� ���� ��ŭ �ݺ�
			if(currentNode.validDirection[i] == d) { // ���� ��忡�� �� �� �ִ� �����̶� d�� �����̶� ���� �� üũ
				moveToNode = currentNode.neighbors[i];
				break;
			}
		}		
		
		return moveToNode;
	}
	
	void Move(){
		if(targetNode != currentNode && targetNode != null) {
			Vector2 inverseDirection = Vector2.Right; // �Ѹ��� ���� ���� ������ �ݴ����
			switch(direction) {
			case Up:
				inverseDirection = Vector2.Down;
				break;
				
			case Down:
				inverseDirection = Vector2.Up;
				break;
				
			case Left:
				inverseDirection = Vector2.Right;
				break;
				
			case Right:
				inverseDirection = Vector2.Left;
				break;
			case Zero:
				inverseDirection = Vector2.Zero;
				break;
			}
			if(nextDirection == inverseDirection) { // ���� ������ ���� ���� �ִ� ������ �ݴ� �������� üũ
				direction = nextDirection; // �ݴ�������� �Ѹ��� ������ �ٲ���
				//�Ѹ��� �̵������� �ݴ�� ��������� ���� ���� ���� ��嵵 �ݴ�� �ٲ���
				Node tmpNode = targetNode;
				targetNode = previousNode;
				previousNode= tmpNode;
			}
			if(OverShotTarget()) { // Ÿ���� ������
				currentNode = targetNode;
				x = currentNode.x;
				y = currentNode.y;
				
				if(currentNode.otherNode != null) { // ���� ��尡 ��Ż�̶�� ��Ż�� �̵��� �� ���� ��� ����
					x = currentNode.otherNode.x;
					y = currentNode.otherNode.y;
					currentNode = currentNode.otherNode;
				}
				
				Node moveToNode = CanMove(nextDirection); // ���� ���⿡ �� �� �ִ� ��带 ����
				if(moveToNode != null) // �� �� �ִ� ��尡 �ִٸ� ���� ������ ���� �������� ����
					direction = nextDirection;
				else moveToNode = CanMove(direction); // ���ٸ� ����������� �� �� �ִ� ��带 ã��
				
				if(moveToNode != null) { // �ִٸ� Ÿ�ٳ��� ���� ��带 �� ����
					targetNode = moveToNode;
					previousNode = currentNode;
					currentNode = null;
				}
				else direction = Vector2.Zero; // ���ٸ� �Ѹ� ����
			}
			else {
				switch(direction) {
				case Up:
					y += speed;
					break;
				case Down:
					y -= speed;
					break;
				case Left:
					x -= speed;
					break;
				case Right:
					x += speed;
					break;
				default:
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
	
	void ConsumeCoin() { // rectangle�� �Ἥ �����̶� �浹�÷�
		
	}
	
	void UpdateOrientation() {
		switch(direction) {
		case Up:
			imageIndex = 3; // �̹����� ���� �ٶ󺸴� �̹����� �ٲ�
			break;
		case Down:
			imageIndex = 1; // �̹����� �Ʒ��� �ٶ󺸴� �̹����� �ٲ�
			break;
		case Left:
			imageIndex = 2; // �̹����� ������ �ٶ󺸴� �̹����� �ٲ�
			break;
		case Right:
			imageIndex = 0; // �̹����� �������� �ٶ󺸴� �̹����� �ٲ�
			break;
		default:
			 // Zero�� ��� �׳� ���� ������� ����
			break;
		}
	}
	
	// ���� �� ���� �� �����ΰ� �浹�� �Լ�
	
	
	// �׸��� �Լ�
	public void render(Graphics g) {
		g.drawImage(Character.pacman[imageIndex], x, y, null);
	}
}
