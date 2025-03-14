package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        String indexStr = request.getParameter("index");
        String updatedOrder = request.getParameter("order");
        try {
            int index = Integer.parseInt(indexStr);
            if (index >= 0 && index < orders.size() && updatedOrder != null && !updatedOrder.isEmpty()) {
                orders.set(index, updatedOrder);
                response.getWriter().write("{\"message\": \"Order updated at index " + index + "\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Invalid index or order parameter\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid index format\"}");
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
