package servlet;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

/**
 * Servlet implementation class UploadServlet
 */
public class UploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    // 上传文件存储目录
    private static final String UPLOAD_DIRECTORY = "upload";
 
    // 上传配置
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 20; // 20MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 60; // 50MB
    // JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost:3306/reportDB";
    
    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "root";
    static final String PASS = "secret"; 
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        PreparedStatement ps=null;
        // 设置响应内容类型
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String title = "";
        String docType = "<!DOCTYPE html>\n";
        out.println(docType +
        "<html>\n" +
        "<head><title>" + title + "</title></head>\n" +
        "<body bgcolor=\"#f0f0f0\">\n" +
        "<h1 align=\"center\">" + title + "</h1>\n");
        //获取已上传文件在服务器中的路径
        Map<String, Object> fields=new HashMap<String, Object>();
		try {
			fields = this.doUpload(request, response);
		} catch (FileUploadException e1) {
			e1.printStackTrace();
		}
        try{
            // 注册 JDBC 驱动器
            Class.forName("com.mysql.jdbc.Driver");
            
            // 打开一个连接
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            // 执行 SQL 查询
            String sql= "INSERT INTO report (name,phone,address,filepath,description) VALUES(?,?,?,?,?)";
            //实例化 PreparedStatement
            ps = conn.prepareStatement(sql);
            //传入参数，这里的参数来自于一个表单
            ps.setString(1, fields.get("name")+"");
            ps.setString(2, fields.get("phone")+"");
            ps.setString(3, fields.get("address")+"");
            ps.setString(4, fields.get("filePath")+"");
            ps.setString(5, fields.get("description")+"");
            //执行数据库更新操作，不需要SQL语句
            ps.executeUpdate();
            ps.close();
            //获取查询结果
            sql="SELECT id,name,phone,address,filepath,description FROM report";
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            // 展开结果集数据库
            while(rs.next()){
                // 通过字段检索
                int id  = rs.getInt("id");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String address = rs.getString("address");
                String filepath = rs.getString("filepath");
                String description = rs.getString("description");
    
                // 输出数据
                out.println("ID: " + id);
                out.println(", 姓名： " + name);
                out.println(", 电话： " + phone);
                out.println(", 地址：" + address);
                out.println(", 文件路径：" + filepath);
                out.println(", 描述：" + description);
                out.println("<br />");
            }
            out.println("</body></html>");

            // 完成后关闭
            rs.close();
            ps.close();
            conn.close();
        } catch(SQLException se) {
            // 处理 JDBC 错误
            se.printStackTrace();
        } catch(Exception e) {
            // 处理 Class.forName 错误
            e.printStackTrace();
        }finally{
            // 最后是用于关闭资源的块
            try{
                if(ps!=null)
                ps.close();
            }catch(SQLException se2){
            }
            try{
                if(conn!=null)
                conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
       
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        doGet(request, response);
    }
    
	/**
	 * 上传数据及保存文件
	 * 
	 * @throws FileUploadException
	 */
    protected Map<String, Object> doUpload(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException, FileUploadException {
		// 构造临时路径来存储上传的文件
		// 这个路径相对当前应用的目录
    	String uploadPath = request.getServletContext().getRealPath("./");
    	Calendar CD = Calendar.getInstance();
    	int YY = CD.get(Calendar.YEAR) ;
    	int MM = CD.get(Calendar.MONTH)+1;
    	int DD = CD.get(Calendar.DATE);
    	int HH = CD.get(Calendar.HOUR_OF_DAY);
    	int NN = CD.get(Calendar.MINUTE);
    	int SS = CD.get(Calendar.SECOND);
    	int MI = CD.get(Calendar.MILLISECOND);
    	String YYMMDD=UPLOAD_DIRECTORY+File.separator+YY+File.separator+MM+File.separator+DD;
    	uploadPath=uploadPath+YYMMDD;
    	// 如果目录不存在则创建
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) { 
            uploadDir.mkdirs();
        }
		Map<String, Object> map = new HashMap<String, Object>();
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (isMultipart) {
			// 文件上传
			ServletFileUpload upload = new ServletFileUpload();
	        // 设置最大文件上传值
	        upload.setFileSizeMax(MAX_FILE_SIZE);
	        // 设置最大请求值 (包含文件和表单数据)
	        upload.setSizeMax(MAX_REQUEST_SIZE);
	        // 中文处理
	        upload.setHeaderEncoding("UTF-8"); 
	        
			FileItemIterator fileItemIterator = upload.getItemIterator(request);
			while (fileItemIterator.hasNext()) {
				FileItemStream item = fileItemIterator.next();
				String name = item.getFieldName();
				
				if (item.isFormField()) {
					InputStream stream = item.openStream();
					// 普通域
					String value = Streams.asString(stream, "UTF-8");
					map.put(name, value);
					stream.close();
				} else {
					// 文件域
					String fileName = HH+"-"+NN+"-"+SS+"-"+MI+"-"+item.getName();
					String filePath = uploadPath + File.separator + fileName;
					File uploadFile = new File(filePath);
					byte[] buffer = new byte[4096];
					InputStream input = null;
					OutputStream output = null;
					try {
						input = item.openStream();
						output = new BufferedOutputStream(new FileOutputStream(uploadFile));
						int n=0;
						while((n = input.read(buffer))!=-1) {
							output.write(buffer, 0, n);
						}
					} finally {
						if (input != null) {
							try {
								input.close();
							} catch (IOException e) {
							}
						}
						if (output != null) {
							try {
								output.close();
							} catch (IOException e) {
							}
						}
					}
				map.put("filePath", YYMMDD+File.separator+fileName);
				}
			}
		}
		return map;
	}
    
}