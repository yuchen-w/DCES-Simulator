package prosumerAgents;

import java.util.UUID;
import org.apache.log4j.Logger;
import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;
import uk.ac.imperial.presage2.core.simulator.Step;
import uk.ac.imperial.presage2.util.environment.EnvironmentMembersService;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.util.location.Location;
import uk.ac.imperial.presage2.util.location.LocationService;

public class LocationVisualisationPlugin {

    private final Logger logger = Logger.getLogger(LocationVisualisationPlugin.class);

    private final EnvironmentMembersService membersService;
    private final LocationService locService;
    private final EnvironmentSharedStateAccess sharedState;
    private static GridPanel grid;
    private static JFrame frame;
    private final int gridSize;
    private final int spriteSize = 26;

    public LocationVisualisationPlugin() {
        super();
        locService = null;
        membersService = null;
        sharedState = null;
        gridSize = 0;
    }

    private void initFrame() {
        int sizex = gridSize * spriteSize;
        int sizey = (gridSize + 1) * spriteSize - 5;
        grid = new GridPanel(sizex, sizey, gridSize);
        frame = new JFrame();
        frame.setMinimumSize(new Dimension(sizex, sizey));
        frame.setResizable(false);
        frame.setTitle("Human Vs Zombie");
        Graphics g = frame.getContentPane().getGraphics();
        frame.getContentPane().add(grid);
        frame.pack();
        frame.setVisible(true);
    }

    @Inject
    public LocationVisualisationPlugin(EnvironmentSharedStateAccess sharedState,
            EnvironmentServiceProvider serviceProvider, @Named("params.size") int size) throws UnavailableServiceException {
        this.membersService = serviceProvider
                .getEnvironmentService(EnvironmentMembersService.class);
        this.locService = serviceProvider
                .getEnvironmentService(LocationService.class);
        this.sharedState = sharedState;
        this.gridSize = size;
        if (frame == null) {
            initFrame();
        }
    }

    @Step
    public void incrementTime(int t) {
        synchronized (LocationVisualisationPlugin.class) {
            grid.players.clear();
            for (UUID pid : this.membersService.getParticipants()) {
                Location l;
                boolean alive = true;
                try {
                    l = this.locService.getAgentLocation(pid);
                } catch (Exception e) {
                    logger.debug("Exception getting agent location.", e);
                    continue;
                }
                if (l == null) {
                    continue;
                }
                Serializable s = sharedState.get("alive", pid);
                if (s != null) {
                    alive = (Boolean) s;
                }
                Player p = new Player(alive);
                p.setPosition((int) l.getX() * spriteSize, (int) l.getY() * spriteSize);
                grid.players.add(p);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                logger.debug("Exception trying to sleep 1 second.", e);
            }
            frame.repaint();
        }
    }

    public class GridPanel extends JPanel {

        int x, y;
        public ArrayList<Player> players = new ArrayList<Player>();
        private final ArrayList<Rectangle> cells;

        public GridPanel(int x, int y, int gridSize) {
            this.x = x;
            this.y = y;
            this.cells = new ArrayList<Rectangle>();
            initGrid(x, y, gridSize);
        }

        private void initGrid(int x, int y, int gridSize) {
            int width = x;
            int height = y;

            int cellWidth = width / gridSize;
            int cellHeight = height / gridSize;

            if (cells.isEmpty()) {
                for (int row = 0; row < gridSize; row++) {
                    for (int col = 0; col < gridSize; col++) {
                        Rectangle cell = new Rectangle(
                                (col * cellWidth),
                                (row * cellHeight),
                                cellWidth,
                                cellHeight);
                        cells.add(cell);
                    }
                }
            }
        }

        @Override
        public void paintComponent(Graphics g) {
            synchronized (LocationVisualisationPlugin.class) {
                g.setColor(Color.white);
                g.fillRect(0, 0, x, y);
                g.setColor(Color.GRAY);
                for (Rectangle cell : cells) {
                    ((Graphics2D) g).draw(cell);
                }
                for (Player p : players) {
                    p.draw((Graphics2D) g);
                }

            }
        }

    }

    public class Player {

        private BufferedImage sprite;
        private final Rectangle boundingBox = new Rectangle();

        public Player(boolean alive) {
            try {
                if (alive) {
                    sprite = ImageIO.read(new URL("http://www.iis.ee.ic.ac.uk/~p.petruzzi/presage2tutorial/human.png"));
                } else {
                    sprite = ImageIO.read(new URL("http://www.iis.ee.ic.ac.uk/~p.petruzzi/presage2tutorial/zombie.png"));
                }
            } catch (IOException e) {
                logger.debug("Exception trying to open the image url (check internet connection).", e);
            }
        }

        public void setPosition(int x, int y) {
            boundingBox.x = x;
            boundingBox.y = y;
        }

        public void draw(Graphics2D g) {
            int x = (int) boundingBox.getX();
            int y = (int) boundingBox.getY();
            g.drawImage(sprite, x + 1, y + 1, null);
        }
    }
}
