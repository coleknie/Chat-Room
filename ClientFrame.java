import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.net.*;

public class ClientFrame extends JFrame {

    JFrame self = this;

    static BufferedReader input;
    static Socket socket;
    static Thread myClientThread;
    static DataOutputStream output;


    JLabel nameLabel;
    JTextField usernameField;

    JLabel ipaddylabel;
    JTextField ipaddyField;

    JLabel portLabel;
    JTextField portNumField;

    JButton connectButton;

    JLabel membersListLabel;
    JList<String> usersOnline;
    DefaultListModel<String> baseUserList;

    JTextArea chatLog;
    JLabel chatLogLabel;

    JTextArea writer;
    JLabel compLabel;

    JButton sendButton;

    JScrollPane chatScroller, compositionScroller;

    JPanel everythingPanel;
    JPanel topRowPanel;
    JPanel middleRow;
    JPanel middleLeft;
    JPanel middleRight;


    public ClientFrame() {

        setSize(1000, 750); //4:3
        setTitle("GWack -- GW Slack Simulator (disconnected)");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        everythingPanel = new JPanel();
        everythingPanel.setLayout(new BoxLayout(everythingPanel, BoxLayout.PAGE_AXIS));
        topRowPanel = new JPanel();
        topRowPanel.setLayout(new FlowLayout());

        nameLabel = new JLabel("Name");
        usernameField = new JTextField("[Enter Username]", 20);
        ipaddylabel = new JLabel("IP");
        ipaddyField = new JTextField("ssh-cs2113.adamaviv.com", 20);
        portLabel = new JLabel("Port");
        portNumField = new JTextField("8886");
        connectButton = new JButton("Connect");

        middleRow = new JPanel();
        middleRow.setLayout(new FlowLayout());
        middleLeft = new JPanel();
        middleLeft.setLayout(new BoxLayout(middleLeft, BoxLayout.PAGE_AXIS));
        middleRight = new JPanel();
        middleRight.setLayout(new BoxLayout(middleRight, BoxLayout.PAGE_AXIS));

        membersListLabel = new JLabel("Members Online");
        chatLogLabel = new JLabel("Messages");
        baseUserList = new DefaultListModel<>();
        usersOnline = new JList<>(baseUserList);
        usersOnline.setSize(100, 100);
        chatLog = new JTextArea(20, 50);
        chatLog.setEditable(false);
        chatScroller = new JScrollPane(chatLog);
        chatScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        writer = new JTextArea(20, 50);

        compositionScroller = new JScrollPane(writer);
        compositionScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);


        compLabel = new JLabel("Compose");
        sendButton = new JButton("Send");
        sendButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        topRowPanel.add(nameLabel);
        topRowPanel.add(usernameField);
        topRowPanel.add(ipaddylabel);
        topRowPanel.add(ipaddyField);
        topRowPanel.add(portLabel);
        topRowPanel.add(portNumField);
        topRowPanel.add(connectButton);
        middleRow.add(middleLeft);
        middleRow.add(middleRight);
        middleLeft.add(membersListLabel);
        middleLeft.add(usersOnline);
        middleRight.add(chatLogLabel);
        middleRight.add(chatScroller);
        middleRight.add(compLabel);
        middleRight.add(compositionScroller);
        middleRight.add(sendButton);
        everythingPanel.add(topRowPanel);
        everythingPanel.add(middleRow);
        add(everythingPanel);

        connectButton.addActionListener(e -> {
            String hostname = ipaddyField.getText();
                if (connectButton.getText().equals("Connect")) {
                    //for connecting
                    chatLog.setText("");
                    myClientThread = new Thread(() -> {
                        try {
                            if (!usernameField.getText().isEmpty()) {
                                final int pt = Integer.parseInt(portNumField.getText());
                                try{

                                    SocketAddress sockaddr = new InetSocketAddress(hostname, pt);
                                    socket = new Socket();
                                    socket.connect(sockaddr, 750);//timing out socket if it takes too long
                                    

                                    if(!socket.isConnected()){
                                        JOptionPane.showMessageDialog(this, "Check if Server is on!\n If Server is on, Server Rejected Connection");
                                        return;
                                    }

                                }

                                catch (UnknownHostException uHE) {
                                    JOptionPane.showMessageDialog(this, "Invalid Host!");
                                    return;
                                }
                                
                                catch(NumberFormatException nFE) {
                                    JOptionPane.showMessageDialog(this, "Invalid Port!");
                                    return;
                                }

                                catch(IllegalArgumentException nFE) {
                                    JOptionPane.showMessageDialog(this, "Invalid Port!");
                                    return;
                                }

                                catch (ConnectException cE) {
                                    JOptionPane.showMessageDialog(this, "Server rejected connection!!");
                                    return;
                                }

                                catch (IOException ioE) {
                                    JOptionPane.showMessageDialog(this, "Server rejected connection!!");
                                    return;
                                }

                                catch (Exception ee) {
                                    JOptionPane.showMessageDialog(this, "Unknown Error! Check if server is running!");
                                    return;
                                }

                                try{

                                    connectButton.setText("Disconnect");
                                    setTitle("GWack -- GW Slack Simulator (connected)");

                                    InputStream stream = socket.getInputStream();
                                    input = new BufferedReader(new InputStreamReader(stream));

                                    output = new DataOutputStream(socket.getOutputStream());


                                    output.writeBytes(
                                    "SECRET\n"+
                                    "3c3c4ac618656ae32b7f3431e75f7b26b1a14a87\n"+
                                    "NAME\n"+
                                    usernameField.getText()+
                                    "\n");
                                
                                }catch (IOException ioE) {
                                    JOptionPane.showMessageDialog(this, "Error! Close and Restart");
                                    return;
                                };

                                if(socket.isConnected()){
                                    usernameField.setEditable(false);
                                    portNumField.setEditable(false);
                                    ipaddyField.setEditable(false);
                                }

                                listenFromChannel();
                            }
                            else{JOptionPane.showMessageDialog(this, "Invalid/Empty Username! Enter Something and Try again!");;
                        }
                        } catch(NumberFormatException invalidPort) {
                            JOptionPane.showMessageDialog(this, "Invalid Port!");
                        } 
                    });
                    myClientThread.start();
                }//for disconnecting
                else {
                    baseUserList.clear();
                    connectButton.setText("Connect");
                    setTitle("GWack -- GW Slack Simulator (disconnected)");
                    try {
                        socket.close();
                        chatLog.setText("");
                    } catch(Exception unknownException) {
                        JOptionPane.showMessageDialog(this, "Error Closing Connection!");
                    }
                    usernameField.setEditable(true);
                    portNumField.setEditable(true);
                    ipaddyField.setEditable(true);
                }

        });

        //for sending messages with button
        sendButton.addActionListener(e -> {
            if (socket != null && socket.isConnected()) {
                try {
                    sendMsg();
                } catch (IOException ioException) {
                    JOptionPane.showMessageDialog(this, "Error sending Message!");
                }
            }
        });

        //for sending with enter
        writer.addKeyListener(
            new KeyListener(){
                public void keyTyped(KeyEvent e) {
                    if (e.getKeyChar() == KeyEvent.VK_ENTER && socket.isConnected()) {
                        try {
                            writer.setText(writer.getText().replace('\n','\0'));
                            sendMsg();
                        } catch(IOException exc) {
                            JOptionPane.showMessageDialog(self, "Error Sending Message!");
                        }
                    }
                }

                public void keyPressed(KeyEvent e){}

                public void keyReleased(KeyEvent e) {}
            }
            );
        setVisible(true);
    }


    //methods

    //for sending all messages (called by other methods)
    public void sendMsg() throws IOException {
        if(writer.getText().trim().length() > 0) {

            output.writeBytes(writer.getText() + "\n");
            writer.setText("");
        }
    }


    public void listenFromChannel() {
            try {
                while (!socket.isClosed()) {
                    String in;
                    if ((in = input.readLine()) != null) {
                        if (in.equals("START_CLIENT_LIST")) {
                            baseUserList.clear();
                            do
                            {
                                in = input.readLine();
                                if(!in.equals("END_CLIENT_LIST")){
                                    baseUserList.addElement(in);
                                }

                            } while (!(in).equals("END_CLIENT_LIST"));
                        }

                        
                        else {
                            String chatLogStr = chatLog.getText();
                            chatLogStr = chatLogStr + in + "\n";
                            chatLog.setText(chatLogStr);
                        }

                    }
                }
            }catch(IOException e){
                if(connectButton.getText().equalsIgnoreCase("disconnect")){
                    JOptionPane.showMessageDialog(this, "Unknown Error! Check if server is running!");
                }
                return;
            }
        }

}