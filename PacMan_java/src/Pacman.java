
public class Pacman extends GameObject{
	
	public Vector2 direction; // �Ѹ��� �̵� ���� 
	public boolean ghostAte = false; // �Ѹ��� ���ɵ��� ��Ƹ��� �� �ִ� ��������
	public Node currentNode, previousNode, targetNode; // �Ѹ��� ���� ��ġ�ϴ� ���, ������ ��ġ�ߴ� ���, ������ ���
	
	private int speed = 4; // �Ѹ� �̵��ӵ�
	private int imageindex = 0; // �Ѹ� �̹��� index (0: ������, 1: �Ʒ���, 2: ����, 3: ����)
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
		//CheckAte();
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

	}
	
	void ConsumeCoin() {
		
	}
}
