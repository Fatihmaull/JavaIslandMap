import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class JavaMap extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel menuPanel;
    private JPanel loginPanel;
    private JPanel mapPanel;
    private JPanel historyPanel;
    private JComboBox<String> startCity;
    private JComboBox<String> endCity;
    private JTextArea outputArea;
    private JTextArea historyArea;
    private JButton findRouteButton;
    private JButton loginButton;
    private JButton guestButton;
    private JButton createAccountButton;
    private JButton backButton;
    private JButton backToMenuButton;
    private JButton viewHistoryButton;
    private JTextField idField;
    private JComboBox<String> cityComboBox;
    private Map<String, Point> cities;
    private Map<String, Map<String, Integer>> distances;
    private Map<String, User> users;
    private User currentUser;
    private boolean isGuest = false;

    public JavaMap() {
        setTitle("Jawir On Trip");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        cities = new HashMap<>();
        distances = new HashMap<>();
        users = new HashMap<>();
        setupCities();
        setupDistances();

        setupMenuPanel();
        setupLoginPanel();
        setupMapPanel();
        setupHistoryPanel();

        mainPanel.add(menuPanel, "Menu");
        mainPanel.add(loginPanel, "Login");
        mainPanel.add(mapPanel, "Map");
        mainPanel.add(historyPanel, "History");

        add(mainPanel);
        cardLayout.show(mainPanel, "Menu");

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                mapPanel.repaint();
            }
        });
    }

    private void setupMenuPanel() {
        menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(3, 1));

        loginButton = new JButton("Login");
        guestButton = new JButton("Guest");
        viewHistoryButton = new JButton("Lihat History");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Login");
            }
        });

        guestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isGuest = true;
                currentUser = new User("Guest", "Jakarta");
                cardLayout.show(mainPanel, "Map");
                startCity.setSelectedItem("Jakarta");
            }
        });

        viewHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateHistoryArea();
                cardLayout.show(mainPanel, "History");
            }
        });

        menuPanel.add(loginButton);
        menuPanel.add(guestButton);
        menuPanel.add(viewHistoryButton);
    }

    private void setupLoginPanel() {
        loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(6, 1));

        idField = new JTextField();
        cityComboBox = new JComboBox<>(cities.keySet().toArray(new String[0]));
        createAccountButton = new JButton("Create Account and Login");
        backToMenuButton = new JButton("Kembali");

        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText();
                String city = (String) cityComboBox.getSelectedItem();
                if (cities.containsKey(city)) {
                    users.put(id, new User(id, city));
                    currentUser = users.get(id);
                    isGuest = false;
                    cardLayout.show(mainPanel, "Map");
                    startCity.setSelectedItem(city);
                } else {
                    JOptionPane.showMessageDialog(null, "Kota tidak ditemukan. Silakan coba lagi.");
                }
            }
        });

        backToMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Menu");
            }
        });

        loginPanel.add(new JLabel("User ID:"));
        loginPanel.add(idField);
        loginPanel.add(new JLabel("Kota Asal:"));
        loginPanel.add(cityComboBox);
        loginPanel.add(createAccountButton);
        loginPanel.add(backToMenuButton);
    }

    private void setupMapPanel() {
        mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawMap(g);
            }
        };

        startCity = new JComboBox<>(cities.keySet().toArray(new String[0]));
        endCity = new JComboBox<>(cities.keySet().toArray(new String[0]));

        outputArea = new JTextArea(5, 30);
        outputArea.setEditable(false);
        findRouteButton = new JButton("Temukan Rute");
        findRouteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findShortestPath();
            }
        });

        backButton = new JButton("Kembali");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Menu");
            }
        });

        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Kota Awal:"));
        controlPanel.add(startCity);
        controlPanel.add(new JLabel("Kota Akhir:"));
        controlPanel.add(endCity);
        controlPanel.add(findRouteButton);
        controlPanel.add(backButton);

        mapPanel.setLayout(new BorderLayout());
        mapPanel.add(controlPanel, BorderLayout.NORTH);
        mapPanel.add(new JScrollPane(outputArea), BorderLayout.SOUTH);
    }

    private void setupHistoryPanel() {
        historyPanel = new JPanel(new BorderLayout());

        historyArea = new JTextArea(20, 50);
        historyArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(historyArea);

        backToMenuButton = new JButton("Kembali ke Menu");
        backToMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Menu");
            }
        });

        historyPanel.add(scrollPane, BorderLayout.CENTER);
        historyPanel.add(backToMenuButton, BorderLayout.SOUTH);
    }

    private void setupCities() {
        cities.put("Jakarta", new Point(100, 100));
        cities.put("Bogor", new Point(120, 140));
        cities.put("Bandung", new Point(150, 180));
        cities.put("Cirebon", new Point(250, 150));
        cities.put("Tegal", new Point(275, 200));
        cities.put("Semarang", new Point(300, 100));
        cities.put("Solo", new Point(350, 250));
        cities.put("Yogyakarta", new Point(350, 300));
        cities.put("Surabaya", new Point(500, 200));
        cities.put("Malang", new Point(550, 300));
        cities.put("Banten", new Point(80, 80));
        cities.put("Bekasi", new Point(120, 90));
        cities.put("Surakarta", new Point(360, 240));
        cities.put("Banyuwangi", new Point(600, 350));

        // Menambahkan kota di pulau lain
        cities.put("Medan", new Point(50, 50)); // Sumatra
        cities.put("Palembang", new Point(150, 50)); // Sumatra
        cities.put("Pontianak", new Point(200, 50)); // Kalimantan
        cities.put("Balikpapan", new Point(300, 50)); // Kalimantan
        cities.put("Makassar", new Point(400, 50)); // Sulawesi
        cities.put("Manado", new Point(500, 50)); // Sulawesi
        cities.put("Denpasar", new Point(400, 300)); // Bali
        cities.put("Ambon", new Point(600, 50)); // Maluku
        cities.put("Jayapura", new Point(700, 50)); // Papua
    }

    private void setupDistances() {
        distances.put("Jakarta", Map.ofEntries(
                Map.entry("Bogor", 60),
                Map.entry("Bandung", 150),
                Map.entry("Cirebon", 250),
                Map.entry("Semarang", 450),
                Map.entry("Banten", 70),
                Map.entry("Bekasi", 20),
                Map.entry("Palembang", 400),
                Map.entry("Pontianak", 800),
                Map.entry("Makassar", 1400),
                Map.entry("Denpasar", 1150),
                Map.entry("Ambon", 2200),
                Map.entry("Jayapura", 3400)));
        distances.put("Bogor", Map.ofEntries(
                Map.entry("Jakarta", 60),
                Map.entry("Bandung", 120),
                Map.entry("Cirebon", 190),
                Map.entry("Bekasi", 50)));
        distances.put("Bandung", Map.ofEntries(
                Map.entry("Jakarta", 150),
                Map.entry("Bogor", 120),
                Map.entry("Cirebon", 140),
                Map.entry("Yogyakarta", 400),
                Map.entry("Surakarta", 350)));
        distances.put("Cirebon", Map.ofEntries(
                Map.entry("Jakarta", 250),
                Map.entry("Bogor", 190),
                Map.entry("Bandung", 140),
                Map.entry("Tegal", 100),
                Map.entry("Semarang", 200)));
        distances.put("Tegal", Map.ofEntries(
                Map.entry("Cirebon", 100),
                Map.entry("Semarang", 100),
                Map.entry("Solo", 200)));
        distances.put("Semarang", Map.ofEntries(
                Map.entry("Jakarta", 450),
                Map.entry("Cirebon", 200),
                Map.entry("Tegal", 100),
                Map.entry("Solo", 100),
                Map.entry("Surabaya", 350),
                Map.entry("Balikpapan", 1000),
                Map.entry("Manado", 1600)));
        distances.put("Solo", Map.ofEntries(
                Map.entry("Tegal", 200),
                Map.entry("Semarang", 100),
                Map.entry("Yogyakarta", 60),
                Map.entry("Surabaya", 300)));
        distances.put("Yogyakarta", Map.ofEntries(
                Map.entry("Bandung", 400),
                Map.entry("Solo", 60),
                Map.entry("Surabaya", 330),
                Map.entry("Malang", 400)));
        distances.put("Surabaya", Map.ofEntries(
                Map.entry("Semarang", 350),
                Map.entry("Solo", 300),
                Map.entry("Yogyakarta", 330),
                Map.entry("Malang", 90),
                Map.entry("Banyuwangi", 300),
                Map.entry("Medan", 2000),
                Map.entry("Makassar", 1000),
                Map.entry("Denpasar", 400),
                Map.entry("Ambon", 1800),
                Map.entry("Jayapura", 3000)));
        distances.put("Malang", Map.ofEntries(
                Map.entry("Yogyakarta", 400),
                Map.entry("Surabaya", 90),
                Map.entry("Banyuwangi", 280)));
        distances.put("Banten", Map.ofEntries(
                Map.entry("Jakarta", 70)));
        distances.put("Bekasi", Map.ofEntries(
                Map.entry("Jakarta", 20),
                Map.entry("Bogor", 50)));
        distances.put("Surakarta", Map.ofEntries(
                Map.entry("Bandung", 350)));
        distances.put("Banyuwangi", Map.ofEntries(
                Map.entry("Surabaya", 300),
                Map.entry("Malang", 280)));

        // Adding distances for cities on other islands
        distances.put("Medan", Map.ofEntries(
                Map.entry("Surabaya", 2000)));
        distances.put("Palembang", Map.ofEntries(
                Map.entry("Jakarta", 400)));
        distances.put("Pontianak", Map.ofEntries(
                Map.entry("Jakarta", 800)));
        distances.put("Balikpapan", Map.ofEntries(
                Map.entry("Semarang", 1000)));
        distances.put("Makassar", Map.ofEntries(
                Map.entry("Jakarta", 1400),
                Map.entry("Surabaya", 1000)));
        distances.put("Manado", Map.ofEntries(
                Map.entry("Semarang", 1600)));
        distances.put("Denpasar", Map.ofEntries(
                Map.entry("Jakarta", 1150),
                Map.entry("Surabaya", 400)));
        distances.put("Ambon", Map.ofEntries(
                Map.entry("Jakarta", 2200),
                Map.entry("Surabaya", 1800)));
        distances.put("Jayapura", Map.ofEntries(
                Map.entry("Jakarta", 3400),
                Map.entry("Surabaya", 3000)));
    }

    private void drawMap(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.BLACK);
        for (String city : distances.keySet()) {
            Point start = cities.get(city);
            for (Map.Entry<String, Integer> entry : distances.get(city).entrySet()) {
                String destination = entry.getKey();
                int distance = entry.getValue();
                Point end = cities.get(destination);

                // Draw the edge line
                g2d.drawLine(start.x, start.y, end.x, end.y);

                // Calculate mid-point for the distance label
                int midX = (start.x + end.x) / 2;
                int midY = (start.y + end.y) / 2;

                // Offset the distance label to avoid overlapping
                midY -= 10;

                // Draw the distance label
                g2d.drawString(String.valueOf(distance) + " KM", midX, midY);
            }
        }

        for (String city : cities.keySet()) {
            Point point = cities.get(city);
            g2d.setColor(Color.RED);
            g2d.fillOval(point.x - 5, point.y - 5, 10, 10);
            g2d.setColor(Color.BLACK);
            g2d.drawString(city, point.x + 5, point.y - 5);
        }
    }

    private void findShortestPath() {
        String start = (String) startCity.getSelectedItem();
        String end = (String) endCity.getSelectedItem();
        if (start == null || end == null || start.equals(end)) {
            outputArea.setText("Silakan pilih kota awal dan kota akhir yang berbeda.");
            return;
        }

        Result result = dijkstraShortestPath(start, end);
        if (result.path == null) {
            outputArea.setText("Rute tidak ditemukan.");
        } else {
            StringBuilder route = new StringBuilder();
            for (String city : result.path) {
                route.append(city).append(" -> ");
            }
            route.setLength(route.length() - 4); // Remove the last arrow
            outputArea.setText("Rute terpendek: " + route.toString() + " (Jarak: " + result.distance + " KM)");

            // Save the route history for the current user
            saveRouteHistory(start, end, route.toString(), result.distance);
        }
    }

    private Result dijkstraShortestPath(String start, String end) {
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(node -> node.distance));
        Map<String, Integer> distancesMap = new HashMap<>();
        Map<String, String> previousMap = new HashMap<>();
        Set<String> visited = new HashSet<>();

        for (String city : cities.keySet()) {
            distancesMap.put(city, Integer.MAX_VALUE);
        }
        distancesMap.put(start, 0);
        pq.add(new Node(start, 0));

        while (!pq.isEmpty()) {
            Node currentNode = pq.poll();
            String currentCity = currentNode.city;

            if (!visited.add(currentCity)) {
                continue;
            }

            if (currentCity.equals(end)) {
                break;
            }

            for (Map.Entry<String, Integer> neighborEntry : distances.getOrDefault(currentCity, Collections.emptyMap())
                    .entrySet()) {
                String neighborCity = neighborEntry.getKey();
                int edgeWeight = neighborEntry.getValue();

                if (visited.contains(neighborCity)) {
                    continue;
                }

                int newDist = distancesMap.get(currentCity) + edgeWeight;
                if (newDist < distancesMap.get(neighborCity)) {
                    distancesMap.put(neighborCity, newDist);
                    pq.add(new Node(neighborCity, newDist));
                    previousMap.put(neighborCity, currentCity);
                }
            }
        }

        if (distancesMap.get(end) == Integer.MAX_VALUE) {
            return new Result(null, Integer.MAX_VALUE);
        }

        List<String> path = new ArrayList<>();
        for (String at = end; at != null; at = previousMap.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return new Result(path, distancesMap.get(end));
    }

    private void saveRouteHistory(String start, String end, String route, int distance) {
        if (currentUser != null) {
            String userName = isGuest ? "Guest" : currentUser.getId();
            currentUser.addHistory(start, end, route, distance, userName);
            updateHistoryArea();
        }
    }

    private void updateHistoryArea() {
        historyArea.setText("");
        for (User user : users.values()) {
            for (String history : user.getHistories()) {
                historyArea.append(history + "\n");
            }
        }
        // Menambahkan histori Guest jika ada
        if (isGuest && currentUser != null) {
            for (String history : currentUser.getHistories()) {
                historyArea.append(history + "\n");
            }
        }
    }

    private class Node {
        String city;
        int distance;

        Node(String city, int distance) {
            this.city = city;
            this.distance = distance;
        }
    }

    private class User {
        private String id;
        private String city;
        private List<String> history;

        User(String id, String city) {
            this.id = id;
            this.city = city;
            this.history = new ArrayList<>();
        }

        String getId() {
            return id;
        }

        void addHistory(String start, String end, String route, int distance, String userName) {
            history.add("User: " + userName + ", Start: " + start + ", End: " + end + ", Route: " + route
                    + ", Distance: " + distance + " KM");
        }

        List<String> getHistories() {
            return history;
        }
    }

    private class Result {
        List<String> path;
        int distance;

        Result(List<String> path, int distance) {
            this.path = path;
            this.distance = distance;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new JavaMap().setVisible(true);
        });
    }
}
