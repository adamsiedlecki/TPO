package pl.asiedlecki;

import org.json.JSONArray;
import org.json.JSONObject;
import pl.asiedlecki.model.Samochod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SerwletWyszukujacyDane extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json = String.join("", Files.readAllLines(Paths.get(getServletContext().getRealPath("samochody.json"))));
        JSONArray array = new JSONArray(json);
        List<Samochod> samochody = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            samochody.add(new Samochod(jsonObject.getString("rodzaj"), jsonObject.getString("marka"), jsonObject.getString("model"), jsonObject.getInt("rok_produkcji")));

        }
        String rodzaj = request.getParameter("rodzajSamochodu");
        System.out.println("Rodzaj samochodu: " + rodzaj);
        if (!rodzaj.isEmpty()) {
            samochody = samochody.stream().filter(s -> s.getRodzaj().equals(rodzaj)).collect(Collectors.toList());
        }

        request.setAttribute("samochody", samochody);
        request.getRequestDispatcher("/tabelka").forward(request, response);
    }
}
