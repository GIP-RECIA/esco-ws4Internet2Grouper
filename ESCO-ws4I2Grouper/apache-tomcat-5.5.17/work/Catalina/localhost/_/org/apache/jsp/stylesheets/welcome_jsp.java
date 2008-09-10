package org.apache.jsp.stylesheets;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class welcome_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.List _jspx_dependants;

  static {
    _jspx_dependants = new java.util.ArrayList(2);
    _jspx_dependants.add("/stylesheets/_include.jsp");
    _jspx_dependants.add("/WEB-INF/esup-commons.tld");
  }

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_e_page_footer;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_e_paragraph_value_escape_nobody;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_e_page_footer = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_e_paragraph_value_escape_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_e_page_footer.release();
    _jspx_tagPool_e_paragraph_value_escape_nobody.release();
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    JspFactory _jspxFactory = null;
    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;


    try {
      _jspxFactory = JspFactory.getDefaultFactory();
      response.setContentType("text/html; charset=ISO-8859-1");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write('\r');
      out.write('\n');
      out.write('\n');
      if (_jspx_meth_e_page_0(_jspx_page_context))
        return;
      out.write('\r');
      out.write('\n');
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
      }
    } finally {
      if (_jspxFactory != null) _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }

  private boolean _jspx_meth_e_page_0(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  e:page
    org.esupportail.commons.web.tags.PageTag _jspx_th_e_page_0 = (org.esupportail.commons.web.tags.PageTag) _jspx_tagPool_e_page_footer.get(org.esupportail.commons.web.tags.PageTag.class);
    _jspx_th_e_page_0.setPageContext(_jspx_page_context);
    _jspx_th_e_page_0.setParent(null);
    _jspx_th_e_page_0.setFooter("");
    int _jspx_eval_e_page_0 = _jspx_th_e_page_0.doStartTag();
    if (_jspx_eval_e_page_0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      if (_jspx_eval_e_page_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
        out = _jspx_page_context.pushBody();
        _jspx_th_e_page_0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
        _jspx_th_e_page_0.doInitBody();
      }
      do {
        out.write('\n');
        out.write('	');
        if (_jspx_meth_e_paragraph_0(_jspx_th_e_page_0, _jspx_page_context))
          return true;
        out.write('\r');
        out.write('\n');
        int evalDoAfterBody = _jspx_th_e_page_0.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
      if (_jspx_eval_e_page_0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
        out = _jspx_page_context.popBody();
      }
    }
    if (_jspx_th_e_page_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_e_page_footer.reuse(_jspx_th_e_page_0);
      return true;
    }
    _jspx_tagPool_e_page_footer.reuse(_jspx_th_e_page_0);
    return false;
  }

  private boolean _jspx_meth_e_paragraph_0(javax.servlet.jsp.tagext.JspTag _jspx_th_e_page_0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  e:paragraph
    org.esupportail.commons.web.tags.ParagraphTag _jspx_th_e_paragraph_0 = (org.esupportail.commons.web.tags.ParagraphTag) _jspx_tagPool_e_paragraph_value_escape_nobody.get(org.esupportail.commons.web.tags.ParagraphTag.class);
    _jspx_th_e_paragraph_0.setPageContext(_jspx_page_context);
    _jspx_th_e_paragraph_0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_e_page_0);
    _jspx_th_e_paragraph_0.setEscape("false");
    _jspx_th_e_paragraph_0.setValue("#{tc.text}");
    int _jspx_eval_e_paragraph_0 = _jspx_th_e_paragraph_0.doStartTag();
    if (_jspx_th_e_paragraph_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_e_paragraph_value_escape_nobody.reuse(_jspx_th_e_paragraph_0);
      return true;
    }
    _jspx_tagPool_e_paragraph_value_escape_nobody.reuse(_jspx_th_e_paragraph_0);
    return false;
  }
}
