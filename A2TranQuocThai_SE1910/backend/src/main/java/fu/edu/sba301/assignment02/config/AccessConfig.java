package fu.edu.sba301.assignment02.config;
import fu.edu.sba301.assignment02.repository.SystemAccountRepository;
import jakarta.servlet.http.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;
@Configuration
public class AccessConfig implements WebMvcConfigurer {
 private final SystemAccountRepository accounts;
 public AccessConfig(SystemAccountRepository accounts){this.accounts=accounts;}
 public void addInterceptors(InterceptorRegistry registry){registry.addInterceptor(new HandlerInterceptor(){
  public boolean preHandle(HttpServletRequest req,HttpServletResponse res,Object handler) throws Exception {
   String path=req.getRequestURI();
   if(req.getMethod().equals("OPTIONS") || path.equals("/api/auth/login") || path.equals("/api/news/active") || (req.getMethod().equals("GET") && (path.equals("/api/categories/active") || path.equals("/api/tags")))) return true;
   Object id=req.getSession().getAttribute("accountId");
   var account=id instanceof Short ? accounts.findById((Short)id).orElse(null):null;
   if(account==null){res.sendError(401);return false;}
   req.setAttribute("account",account);
   int role=path.startsWith("/api/accounts")?1:2;
   if(!path.startsWith("/api/auth/") && account.getAccountRole()!=role){res.sendError(403);return false;}
   return true;
  }
 }).addPathPatterns("/api/**");}
}
