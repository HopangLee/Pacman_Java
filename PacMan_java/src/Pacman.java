import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.awt.event.KeyEvent;

import javax.imageio.ImageIO;

public class Pacman extends GameObject {

	public int score = 0; // ���߿� gameŬ�󽺳� gameboardŬ���� score�� ��ü
	int combo = 1; // ������ �������� ������ ������ ���
	int ghostAteTimer = 3 * 60;
	int comboTime = 150; //(2.5�� * 60)
	public boolean ghostAte = false; // �Ѹ��� ���ɵ��� ��� �Ծ�����

	public Vector2 direction; // �Ѹ��� �̵� ����
	public Node currentNode, previousNode, targetNode; // �Ѹ��� ���� ��ġ�ϴ� ���, ������ ��ġ�ߴ� ���, ������ ���

	private int speed = 3; // �Ѹ� �̵��ӵ�

	// ��������Ʈ ����
	private static BufferedImage[] pacmanSprite;
	private int imageIndex = 0; // �Ѹ� �̹��� index (0: ������, 4: ����, 8: ����, 12: �Ʒ���)
	private int animation_index = 0; // �Ѹ� �ִϸ��̼� index (0,1,2,1)

	private Vector2 nextDirection; // ����Ű�� ������ �Ѹ��� ������ ����

	private GameBoard board;

	public Pacman(GameBoard board) {
		this.board = board; // ��� ��带 ��� �ִ� ���Ӻ��带 ����
		pacmanSprite = new BufferedImage[16];
		try {
			pacmanSprite[0] = ImageIO.read(getClass().getResource("/image/PacMan0.PNG"));
			pacmanSprite[1] = ImageIO.read(getClass().getResource("/image/PacMan1.PNG"));
			pacmanSprite[2] = ImageIO.read(getClass().getResource("/image/PacMan2.PNG"));
			pacmanSprite[3] = ImageIO.read(getClass().getResource("/image/PacMan1.PNG"));
			pacmanSprite[4] = ImageIO.read(getClass().getResource("/image/PacMan3.PNG"));
			pacmanSprite[5] = ImageIO.read(getClass().getResource("/image/PacMan4.PNG"));
			pacmanSprite[6] = ImageIO.read(getClass().getResource("/image/PacMan5.PNG"));
			pacmanSprite[7] = ImageIO.read(getClass().getResource("/image/PacMan4.PNG"));
			pacmanSprite[8] = ImageIO.read(getClass().getResource("/image/PacMan6.PNG"));
			pacmanSprite[9] = ImageIO.read(getClass().getResource("/image/PacMan7.PNG"));
			pacmanSprite[10] = ImageIO.read(getClass().getResource("/image/PacMan8.PNG"));
			pacmanSprite[11] = ImageIO.read(getClass().getResource("/image/PacMan7.PNG"));
			pacmanSprite[12] = ImageIO.read(getClass().getResource("/image/PacMan9.PNG"));
			pacmanSprite[13] = ImageIO.read(getClass().getResource("/image/PacMan10.PNG"));
			pacmanSprite[14] = ImageIO.read(getClass().getResource("/image/PacMan11.PNG"));
			pacmanSprite[15] = ImageIO.read(getClass().getResource("/image/PacMan10.PNG"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start() {
		currentNode = board.nodes[69];
		x = currentNode.x;
		y = currentNode.y;

		setBounds(x, y, 27, 27);

		direction = Vector2.Right;
		changePosition(direction);
	}

	@Override
	public void update() {
		CheckInput(); // ����ڰ� � Ű�� �Է��ߴ��� �� �����Ӹ��� üũ
		Move(); // �Ѹ��� ��� ������
		ConsumeCoin();
		UpdateOrientation(); // �Ѹ��� �ٶ󺸴� ������ �̹����� �ٲ���

		// �ִϸ��̼� �ε��� ����
		if (animation_index < 9) {
			animation_index++;
		} else
			animation_index = 0;

		/********************************* �浹 ���� ************************************/
		// Coin�� �浹�� Coin�����
		for (int i = 0; i < board.coins.size(); i++) {
			if (this.intersects(board.coins.get(i))) {
				score += 10;
				EventQueue.pushEvent(GameEvent.EventType.RemoveObject, board.coins.get(i));
				// System.out.println("���ΰ� �浹");
				break;
			}
		}

		// BigCoin�� �浹�� BigCoin������� ���ÿ� ������ �������·� ��ȯ
		for (int i = 0; i < board.bigCoins.size(); i++) {
			if (this.intersects(board.bigCoins.get(i))) {
				score += 50;
				EventQueue.pushEvent(GameEvent.EventType.RemoveObject, board.bigCoins.get(i));

				board.ghosts.get(0).StartFrightenedMode();
				board.ghosts.get(1).StartFrightenedMode();
				board.ghosts.get(2).StartFrightenedMode();
				board.ghosts.get(3).StartFrightenedMode();

				// System.out.println("�� ���ΰ� �浹");
				break;
			}
		}

		// ��� coin�� �� ���� -> Game Clear
		if (board.coins.size() == 0 && board.bigCoins.size() == 0) {
			EventQueue.pushEvent(GameEvent.EventType.GameClear, null);
			// System.out.println("Game Clear!");
			return;
		}

		for (int i = 0; i < board.ghosts.size(); i++) {
			Ghost temp = board.ghosts.get(i);
			if (this.intersects(temp)) {
				if (temp.currentMode != Ghost.Mode.Consumed) { // �̹� ���� ������ �ƴϰ�
					// System.out.println("���ɰ� �浹");
					if (temp.currentMode == Ghost.Mode.frighted) { // �̿� ���� �����̶�� ������ ����
						int getScore = 200 * combo;
						score += getScore;
						combo *= 2;
						ghostAteTimer = 0;
						ghostAte = true;
						temp.Consumed(getScore);
					} else { // �ƴ϶�� �Ѹ��� ����
								// Game Over (��� 3��?)
						EventQueue.pushEvent(GameEvent.EventType.GameOver, null);
						System.out.println("Game Over!");
						return;
					}
				}
			}
		}

		if (ghostAte) {
			ghostAteTimer++;

			if (ghostAteTimer > comboTime) {
				combo = 1;
				ghostAte = false;
			}
		}
	}

	private void CheckInput() {
		if (Input.getKey(KeyEvent.VK_RIGHT)) {
			changePosition(Vector2.Right);
		} else if (Input.getKey(KeyEvent.VK_LEFT)) {
			changePosition(Vector2.Left);
		} else if (Input.getKey(KeyEvent.VK_UP)) {
			changePosition(Vector2.Up);
		} else if (Input.getKey(KeyEvent.VK_DOWN)) {
			changePosition(Vector2.Down);
		}
	}

	Node GetNodePosition() {

		return null;
	}

	// d�������� �Ѹ��� ������ �ٲ�(������ ��)
	void changePosition(Vector2 d) {
		if (direction != d) { // �Ѹ��� ���� �ִ� ����� �ٸ� �������� üũ
			nextDirection = d; // �Ѹ��� �� ������ d
		}
		if (currentNode != null) {
			Node moveToNode = CanMove(d); // d �������� �� �� �ִٸ� moveToNode�� �� �� �ִ� ��尡 ����

			if (moveToNode != null) {
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

		for (int i = 0; i < currentNode.length; i++) { // ���� ��忡�� �̿��� ��� ���� ��ŭ �ݺ�
			if (currentNode.validDirection[i] == d) { // ���� ��忡�� �� �� �ִ� �����̶� d�� �����̶� ���� �� üũ
				moveToNode = currentNode.neighbors[i];
				if (moveToNode == board.nodes[31]) {
					moveToNode = null;
				}
				break;
			}
		}

		return moveToNode;
	}

	void Move() {
		if (targetNode != currentNode && targetNode != null) {
			Vector2 inverseDirection = Vector2.Right; // �Ѹ��� ���� ���� ������ �ݴ����
			switch (direction) {
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
			if (nextDirection == inverseDirection) { // ���� ������ ���� ���� �ִ� ������ �ݴ� �������� üũ
				direction = nextDirection; // �ݴ�������� �Ѹ��� ������ �ٲ���
				// �Ѹ��� �̵������� �ݴ�� ��������� ���� ���� ���� ��嵵 �ݴ�� �ٲ���
				Node tmpNode = targetNode;
				targetNode = previousNode;
				previousNode = tmpNode;
			}
			if (OverShotTarget()) { // Ÿ���� ������
				currentNode = targetNode;
				x = currentNode.x;
				y = currentNode.y;

				if (currentNode.otherNode != null) { // ���� ��尡 ��Ż�̶�� ��Ż�� �̵��� �� ���� ��� ����
					x = currentNode.otherNode.x;
					y = currentNode.otherNode.y;
					currentNode = currentNode.otherNode;
				}

				Node moveToNode = CanMove(nextDirection); // ���� ���⿡ �� �� �ִ� ��带 ����
				if (moveToNode != null) // �� �� �ִ� ��尡 �ִٸ� ���� ������ ���� �������� ����
					direction = nextDirection;
				else
					moveToNode = CanMove(direction); // ���ٸ� ����������� �� �� �ִ� ��带 ã��

				if (moveToNode != null) { // �ִٸ� Ÿ�ٳ��� ���� ��带 �� ����
					targetNode = moveToNode;
					previousNode = currentNode;
					currentNode = null;
				} else
					direction = Vector2.Zero; // ���ٸ� �Ѹ� ����
			} else {
				switch (direction) {
				case Up:
					imageIndex = 3;
					y -= speed;
					break;
				case Down:
					imageIndex = 9;
					y += speed;
					break;
				case Left:
					imageIndex = 6;
					x -= speed;
					break;
				case Right:
					imageIndex = 0;
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
		float fx = (float) targetx;
		float fy = (float) targety;

		// ����: x^2 + y^2�� ��ȯ
		return (fx - (float) previousNode.x) * (fx - (float) previousNode.x)
				+ (fy - (float) previousNode.y) * (fy - (float) previousNode.y);
	}

	void ConsumeCoin() { // rectangle�� �Ἥ �����̶� �浹�÷�

	}

	void UpdateOrientation() {
		switch (direction) {
		case Up:
			imageIndex = 4; // �̹����� ���� �ٶ󺸴� �̹����� �ٲ�
			break;
		case Down:
			imageIndex = 12; // �̹����� �Ʒ��� �ٶ󺸴� �̹����� �ٲ�
			break;
		case Left:
			imageIndex = 8; // �̹����� ������ �ٶ󺸴� �̹����� �ٲ�
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
	@Override
	public void render(Graphics g) {
		g.drawImage(pacmanSprite[imageIndex + animation_index / 3], x, y, null);

		// Score �ؽ�Ʈ
		// g.setColor(Color.BLACK);
		// g.fillRect(30, 860, 150, 70);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Press Start 2P", Font.BOLD, 28));
		g.drawString(String.valueOf(score), 32, 780);
	}
}
