
public class Node extends GameObject{ // GameObject�� ��ӹ��� �� x,y��ġ ���� ����
	
	public Node[] neighbors = new Node[4]; // �ش� ��忡�� �� �� �ִ� ��带 ���� (0: ������, 1: �Ʒ�, 2: ����, 3: ����)
	public boolean[] validDirections = new boolean[4]; // �� �� �ִ� ������� ���� (0: ������, 1: �Ʒ�, 2: ����, 3: ����)
	public boolean isPotal = false; // �� ��尡 ��Ż������ Ȯ��
	public Node otherNode = null; // �� ��尡 ��Ż�̶�� ��� ��带 ����
	
	// default Constructer
	public Node() {
		
	}
	
	// neighbor �� �⺻ ����
	public void Setting(Node right, Node down, Node left, Node up, boolean isPotal, Node otherNode, int x, int y) {
		neighbors[0] = right;
		neighbors[1] = down;
		neighbors[2] = left;
		neighbors[3] = up;
		
		validDirections[0] = (right != null);
		validDirections[1] = (down != null);
		validDirections[2] = (left != null);
		validDirections[3] = (up != null);
		
		this.isPotal = isPotal;
		this.otherNode = otherNode;
		
		this.x = x;
		this.y = y;
	}

}
