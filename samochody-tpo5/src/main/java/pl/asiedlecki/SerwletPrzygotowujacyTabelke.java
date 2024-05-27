package pl.asiedlecki;

import pl.asiedlecki.model.Samochod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class SerwletPrzygotowujacyTabelke extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<Samochod> results = (List<Samochod>) request.getAttribute("samochody");

        response.setContentType("text/html");
        response.getWriter().println("<html><body><h1>Tabelka wynikow wyszukiwania samochodow</h1>");
        if (results != null && !results.isEmpty()) {
            response.getWriter().println("<table border='1'><tr> <th>Rodzaj</th> <th>Marka</th> <th>Model</th> <th>rok produkcji</th></tr>");
            for (Samochod samochod : results) {
                response.getWriter().print("<tr>");
                response.getWriter().println("<td>" + samochod.getRodzaj() + "</td>");
                response.getWriter().println("<td>" + samochod.getMarka() + "</td>");
                response.getWriter().println("<td>" + samochod.getModel() + "</td>");
                response.getWriter().println("<td>" + samochod.getRok_produkcji() + "</td>");
                response.getWriter().print("</tr>");
            }
            response.getWriter().println("</table>");
        } else {
            response.getWriter().println("<p>Nie znaleziono samochodow.</p>");
        }
        response.getWriter().println("</body></html>");
    }
}
