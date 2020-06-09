public class Node extends GameObject{ // GameObject�� ��ӹ��� �� x,y��ġ ���� ����
	
	public Node[] temp = new Node[4]; // �ش� ��忡�� �� �� �ִ� ��带 �ӽ� ���� (0: ������, 1: �Ʒ�, 2: ����, 3: ����)
	//public boolean[] canMove = new boolean[4]; // �ش� �������� �� �� �ִ��� (0: ������, 1: �Ʒ�, 2: ����, 3: ����)
	public boolean isPotal = false; // �� ��尡 ��Ż������ Ȯ��
	public Node otherNode = null; // �� ��尡 ��Ż�̶�� ��� ��带 ����
	
	public Node[] neighbors; // �ش� ��忡�� �� �� �ִ� ��常 ����
	public Vector2[] validDirection; // �ش� ��忡�� �� �� �ִ� ���� ����
	public int length = 0; // �̿��� ���� 
	
	// default Constructer
	public Node() {
		
	}
	
	public Node(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	// neighbor �� �⺻ ���� -> ���� ����ȭ �ؾ��� �� (gameboard �밡�� �ٲٱ� �Ⱦ �ϴ� �̷��� ��)
	public void Setting(Node right, Node down, Node left, Node up, boolean isPotal, Node otherNode, int x, int y) {
		temp[0] = right;
		temp[1] = down;
		temp[2] = left;
		temp[3] = up;
		
		/*
		canMove[0] = (right != null);
		canMove[1] = (down != null);
		canMove[2] = (left != null);
		canMove[3] = (up != null);*/
		
		if(right != null) length++;
		if(down != null) length++;
		if(left != null) length++;
		if(up != null) length++;
		
		neighbors = new Node[length];
		validDirection = new Vector2[length];
		
		int j = 0;
		for(int i = 0; i < 4; i++) {
			if(temp[i] != null) {
				neighbors[j] = temp[i];
				switch(i) {
				case 0:
					validDirection[j] = Vector2.Right;
					break;
				case 1:
					validDirection[j] = Vector2.Down;
					break;
				case 2:
					validDirection[j] = Vector2.Left;
					break;
				case 3:
					validDirection[j] = Vector2.Up;
					break;
				}
				j++;
			}
		}
		
		this.isPotal = isPotal;
		this.otherNode = otherNode;
		
		this.x = x;
		this.y = y;
	}

}
