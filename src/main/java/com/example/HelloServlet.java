package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;

@WebServlet("/api/orders")
public class HelloServlet extends HttpServlet {

    private static final List<String> orders = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"orders\": " + orders.toString() + "}");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String newOrder = request.getParameter("order");

        if (newOrder != null && !newOrder.isEmpty()) {
            orders.add(newOrder);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write("{\"message\": \"Order added: " + newOrder + "\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Order parameter is missing\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getParameterMap().forEach((key, value) -> System.out.println(key + " = " + String.join(", ", value)));

        String indexStr = request.getParameter("index");
        String updatedOrder = request.getParameter("order");

        // 🔹 Dodatno branje podatkov iz request body
        if (indexStr == null || updatedOrder == null) {
            BufferedReader reader = request.getReader();
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }

            // 🔹 Parsanje parametrov iz telesa zahteve
            String[] params = body.toString().split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    try {
                        String key = URLDecoder.decode(keyValue[0], "UTF-8");  // ✅ Uporabi "UTF-8" namesto StandardCharsets.UTF_8
                        String value = URLDecoder.decode(keyValue[1], "UTF-8");

                        if (key.equals("index")) {
                            indexStr = value;
                        } else if (key.equals("order")) {
                            updatedOrder = value;
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        System.out.println("Decoded indexStr: " + indexStr);
        System.out.println("Decoded updatedOrder: " + updatedOrder);

        if (indexStr == null || updatedOrder == null || updatedOrder.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Missing index or order parameter\"}");
            return;
        }

        try {
            indexStr = indexStr.trim();
            int index = Integer.parseInt(indexStr);

            if (index >= 0 && index < orders.size()) {
                orders.set(index, updatedOrder);
                response.getWriter().write("{\"message\": \"Order updated at index " + index + "\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Invalid index\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid index format. Expected an integer.\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String indexStr = request.getParameter("index");
        try {
            int index = Integer.parseInt(indexStr);
            if (index >= 0 && index < orders.size()) {
                orders.remove(index);
                response.getWriter().write("{\"message\": \"Order deleted at index " + index + "\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Invalid index\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid index format\"}");
        }
    }

}
