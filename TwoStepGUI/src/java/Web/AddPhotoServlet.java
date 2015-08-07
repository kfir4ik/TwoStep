package Web;

import Db.DBHandler;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class AddPhotoServlet extends HttpServlet {

       private static String UPLOAD_DIRECTORY;
       private static DBHandler db_session;
       private static int load_state = 0;
       
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {            
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Add photo to game DB store</title>");            
            out.println("</head>");
            out.println("<body align=\"left\" style=\"background-image: url('misc/bg1.jpg'); background-attachment: fixed; background-size: 100%; background-repeat: no-repeat; background-color: black;\">");
            if (load_state == 0)
            {
                    out.println("<div><h2><p>Invalid input !!!<p></h2><a href='addphoto.jsp'>List Photos </a></div>");                            
            }
            else
            {
                out.println("<br><div><h3><p>Picture was added Successfully.<p></h3><a href='addphoto.jsp'>List Photos </a></div>");            
            }
            out.println("</body>");
            out.println("</html>");
        } finally {            
            out.close();
        }
    }       

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UPLOAD_DIRECTORY = "D:/temp/";
        
        //PrintWriter out = response.getWriter();
        //HttpSession _session = request.getSession(true);                
        
        if (db_session == null)
        {
            try
            {
                db_session = DBHandler.getInstance();  
                
                if (!db_session.is_connected())
                {
                   db_session.connect();
                }
            }
            catch (Exception exception)
            {
                db_session = null;          
                return;
            }
        }           
        
        try {
            // Apache Commons-Fileupload library classes
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload sfu  = new ServletFileUpload(factory);

            if (! ServletFileUpload.isMultipartContent(request)) {
                System.out.println("No file uploaded.");
                return;
            }
            
            List items = sfu.parseRequest(request);
                        
            FileItem title = (FileItem) items.get(0);
            String  phototitle =  title.getString();                 
            
            FileItem file = (FileItem) items.get(1);             
            long fileSize = file.getSize();
            
            if (file != null && fileSize > 0)
            {                   
                file.write(new File(UPLOAD_DIRECTORY + "tmpfile"));                   
                db_session.addImage(file,phototitle,UPLOAD_DIRECTORY);
                load_state = 1;
            }
            
            processRequest(request, response);                                                            
        }
        catch(Exception exception) 
        {

        }
    }
    
    @Override
    public String getServletInfo() {
        return "";
    }
    

}