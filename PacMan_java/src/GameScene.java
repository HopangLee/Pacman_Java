import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.LinkedList;
import java.util.Queue;

public class GameScene extends Scene {
	private Queue<RenderableObject> removeRequests = new LinkedList<RenderableObject>();
	
	
	private GameBoard gameBoard = new GameBoard();
	

	private int pauseMenuIndex = 0;
	private static final String[] pauseMenuStrings = new String[] {"Resume", "Restart", "Back to Main Menu"};

	public GameScene(KeyListener input) {
		super(input);
		setSubtitle("Game");
		addObject(new Map());
		addObjectsFromGameBoard(gameBoard);
		EventQueue.popAllEvents();
	}
	
	public void addObjectsFromGameBoard(GameBoard gb) {
		addObject(gb.pacman);
		addObjects(gb.bigCoins);
		addObjects(gb.coins);
		addObjects(gb.ghosts);
	}	
	
	@Override
	public void update() {
		switch(getRunningState()) {
		case RUNNING:
			if (Input.getKeyDown(KeyEvent.VK_ESCAPE)) {
				EventQueue.pushEvent(GameEvent.EventType.GamePaused, null);
				return;
			}
			super.update();
			for (int i = 0; i < EventQueue.size(); i++) {
				GameEvent e = EventQueue.peekEvent();
				switch(e.getEvent()) {
				case RemoveObject:
					EventQueue.popEvent();
					RenderableObject o = e.getObject();
					System.out.println(o.toString());
					removeObject(o);
					if (o instanceof BigCoin) {
						gameBoard.bigCoins.remove(o);
					} else if (o instanceof Coin) {
						gameBoard.coins.remove(o);
					}
					o = null;
					break;
				case GameClear:
				case GameOver:
				case GamePaused:
					setRunningState(RunningState.PAUSED);
					return;
				}
			}
			break;
		case PAUSED:
			GameEvent e = EventQueue.peekEvent();
			switch(e.getEvent()) {
			case GameClear:
			case GameOver:
				if (Input.getKeyDown(KeyEvent.VK_R)) {
					setRunningState(RunningState.RESTART);
				} else if (Input.getKeyDown(KeyEvent.VK_ENTER)) {
					setRunningState(RunningState.EXIT);
				}
				break;
			case GamePaused:
				if (Input.getKeyDown(KeyEvent.VK_R)) {
					EventQueue.popEvent();
					setRunningState(RunningState.RESTART);
				} else if (Input.getKeyDown(KeyEvent.VK_DOWN)) {
					if (pauseMenuIndex < 2)
						++pauseMenuIndex;
				} else if (Input.getKeyDown(KeyEvent.VK_UP)) {
					if (pauseMenuIndex >= 0)
						--pauseMenuIndex;
				} else if (Input.getKeyDown(KeyEvent.VK_ENTER)) {
					switch(pauseMenuIndex) {
					case 0:
						setRunningState(RunningState.RUNNING);
						break;
					case 1:
						setRunningState(RunningState.RESTART);
						break;
					case 2:
						setRunningState(RunningState.EXIT);
						break;
					}
				} else if (Input.getKeyDown(KeyEvent.VK_ESCAPE)) {
					EventQueue.popEvent();
					setRunningState(RunningState.RUNNING);
				}
				break;
			default:
				System.err.println("Error: Event not available (" + e.getEvent().toString() + ")");
				break;
			}
			break;
		default:
			break;
		}
		/*
		case CLEAR:
			if (Input.getKey(KeyEvent.VK_R)) {
				setRunningState(RunningState.RUNNING);
			} */
	}
	
	@Override
	public void render() {
		BufferStrategy buffer = getBufferStrategy();
		Graphics graphics = buffer.getDrawGraphics();
		switch(getRunningState()) {
		case RUNNING:
			super.render();
			break;
		case PAUSED:
			super.render();
			GameEvent e = EventQueue.peekEvent();
			switch(e.getEvent()) {
			case GameClear:
				// TODO: Ŭ���� ȭ�� ������
				break;
			case GameOver:
				// TODO: ���ӿ��� ȭ�� ������
				break;
			case GamePaused:
				// TODO: ���� ���� �޴� ������
				int centerX = Game.WIDTH / 2;
				int centerY = Game.HEIGHT / 2;
				graphics.setColor(Color.black);
				graphics.fillRect(centerX - 300, centerY - 300, 600, 600);
			    graphics.setFont(new Font(Font.DIALOG, Font.BOLD, 30));
			    for (int i = 0; i < 2; i++) {
			    	if (i == pauseMenuIndex) {
					    graphics.setColor(Color.WHITE);
					    graphics.fillRect(centerX - 210, centerY + 200 * (i - 1) - 10, 420, 50);
					    graphics.setColor(Color.black);
						graphics.drawString(pauseMenuStrings[i], centerX - 200, centerY + 200 * (i - 1));
			    	} else {
					    graphics.setColor(Color.WHITE);
						graphics.drawString(pauseMenuStrings[i], centerX - 200, centerY + 200 * (i - 1));
			    	}
			    }
				break;
			default:
				System.err.println("Error: Event not available (" + e.getEvent().toString() + ")");
				break;
			}
			break;
		default:
			break;
		}
		
		graphics.dispose();
		buffer.show();
	}
}
