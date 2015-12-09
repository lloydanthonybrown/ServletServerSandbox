package ServerSandboxPackage;

import org.quickconnectfamily.json.JSONInputStream;
import org.quickconnectfamily.json.JSONOutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@WebServlet(name = "JSONEchoService", urlPatterns = {"/json"})
public class JSONEchoServlet extends HttpServlet {
    private ApplicationController theAppController = new ApplicationController();

    public void init(){
        theAppController.mapCommand("Speak", new SpeakHandler());
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            JSONInputStream inFromClient = new JSONInputStream(request.getInputStream());
            JSONOutputStream outToClient = new JSONOutputStream(response.getOutputStream());

            // Nasty path: How to simulate a lag on the server.
            // This error is what I got on the client side when I used this sleep()
            // and then manually shut down the server: java.net.SocketException: Connection reset.
//            Thread.currentThread().sleep(100000);

            // Nasty path: connect the client and server initially. Send a message to the server while having this line
            // active in the current position of this server class: Thread.currentThread().sleep(100000);
            // Then kill the client before the server can get the hashmap.
            // Result: The client ended with "process finished with exit code -1". Server gave me no errors. It gave:
            // Just got:{message=sending this, command=Speak} from client
            // just sent {message=sending this, command=Done}

            HashMap<String, Object> dataMap = (HashMap) inFromClient.readObject();
            dataMap.put("toClient", outToClient);

            String aCommand = (String) dataMap.get("command");
            theAppController.handleRequest(aCommand, dataMap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
}
