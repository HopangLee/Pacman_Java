
public class Pacman extends GameObject{

	public int direction = 0; // �Ѹ��� �̵� ���� (0: ������, 1: �Ʒ���, 2: ����, 3:����)
	public boolean ghostAte = false; // �Ѹ��� ���ɵ��� ��Ƹ��� �� �ִ� ��������
	
	private int speed = 4; // �Ѹ� �̵��ӵ�
	private int imageindex = 0; // �Ѹ� �̹��� index (0: ������, 1: �Ʒ���, 2: ����, 3: ����)
	private int animation_index = 0; // �Ѹ� �ִϸ��̼� index
	
	private GameBoard board;
	
	public Pacman(GameBoard board) {
		this.board = board;
	}
	
	@Override
	public void start() {
		//if(node)
	}
	
	@Override
	public void update() {
		
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
	

}
