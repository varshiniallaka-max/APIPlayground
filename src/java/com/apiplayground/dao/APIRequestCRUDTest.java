
package com.apiplayground.dao;

import java.util.List;

public class APIRequestCRUDTest {

    public static void main(String[] args) {

        APIRequestDAO dao = new APIRequestDAO();

        int userId = 1;
        String requestName = "Test API";
        String method = "GET";
        String url = "https://jsonplaceholder.typicode.com/posts";
        String body = "";

        System.out.println("===== CREATE =====");

        int requestId = dao.addRequestForUserAndReturnId(
                userId,
                requestName,
                method,
                url,
                body
        );

        if (requestId <= 0) {
            System.out.println("CREATE FAILED");
            return;
        }

        System.out.println("Request created. ID: " + requestId);

        System.out.println("\n===== READ =====");

        List<String[]> requests = dao.getRequestsByUser(userId);

        for (String[] item : requests) {
            System.out.println(
                    "ID: " + item[0]
                    + ", Name: " + item[1]
                    + ", Method: " + item[2]
                    + ", URL: " + item[3]
            );
        }

        System.out.println("\n===== UPDATE =====");

        boolean updated = dao.updateRequest(
                requestId,
                userId,
                "Updated Test API",
                "GET",
                "https://jsonplaceholder.typicode.com/users",
                ""
        );

        System.out.println(
                updated ? "UPDATE SUCCESS" : "UPDATE FAILED"
        );

        System.out.println("\n===== DELETE =====");

        boolean deleted = dao.deleteRequest(requestId, userId);

        System.out.println(
                deleted ? "DELETE SUCCESS" : "DELETE FAILED"
        );
    }
}
