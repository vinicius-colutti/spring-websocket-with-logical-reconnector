package com.colutti.websocketclient;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockserver.integration.ClientAndServer;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

class YourTestClass {

    private static final HttpClient httpClientMock = mock(HttpClient.class);
    private static final HttpResponse<String> httpResponseMock = mock(HttpResponse.class);
    private static final EcsClient ecsClientMock = mock(EcsClient.class);
    private static final String CLUSTER_ARN = "clusterArn";
    private static final String TASK_DEFINITION_ARN = "taskDefinitionArn";

    private static final int MOCK_SERVER_PORT = findAvailablePort();
    private static final String MOCK_SERVER_HOST = "localhost";
    private static final String MOCK_ENDPOINT = "/endpoint";
    private static final String EXPECTED_RESPONSE = "Mock response";

    private static ClientAndServer mockServer;
    private static HttpClient httpClient;

    private static int findAvailablePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("Failed to find available port", e);
        }
    }

    @BeforeAll
    static void setupMockServer() {
        mockServer = ClientAndServer.startClientAndServer(MOCK_SERVER_PORT);
        httpClient = HttpClient.newHttpClient();
    }

    @AfterAll
    static void stopMockServer() {
        mockServer.stop();
    }

    @Test
    void testGetAllIpsOfService() {
        TestAWS testAWS = new TestAWS();
        // Criar uma resposta simulada com tarefas contendo endereços IP
        Task task1 = Task.builder()
                .taskDefinitionArn(TASK_DEFINITION_ARN)
                .containers(Container.builder()
                        .networkInterfaces(NetworkInterface.builder()
                                .privateIpv4Address("192.168.0.1")
                                .build())
                        .build())
                .build();

        Task task2 = Task.builder()
                .taskDefinitionArn("outroArn")
                .containers(Container.builder()
                        .networkInterfaces(NetworkInterface.builder()
                                .privateIpv4Address("192.168.0.2")
                                .build())
                        .build())
                .build();

        DescribeTasksResponse describeTasksResponse = DescribeTasksResponse.builder()
                .tasks(task1, task2)
                .build();

        // Criar um mock do EcsClient
        EcsClient mockEcsClient = Mockito.mock(EcsClient.class);

        // Configurar o comportamento do mock para retornar a resposta simulada
        Mockito.when(mockEcsClient.describeTasks(Mockito.any(DescribeTasksRequest.class)))
                .thenReturn(describeTasksResponse);

        // Chamar o método getAllIpsOfService
        List<String> ips = testAWS.getAllIpsOfService(mockEcsClient, CLUSTER_ARN, TASK_DEFINITION_ARN);

        // Verificar se a lista de endereços IP está correta
        List<String> expectedIps = Arrays.asList("192.168.0.1");
        assertEquals(expectedIps, ips);
    }

    @Test
    void testMakeRequests() throws IOException, InterruptedException {

        TestAWS testAWS = new TestAWS();

        // Configurar as respostas do MockServer
        mockServer.when(
                        request().withMethod("GET").withPath(MOCK_ENDPOINT).withHeader("ACCOUNT", "valor_do_header"))
                .respond(response().withStatusCode(200).withBody(EXPECTED_RESPONSE));

        when(ecsClientMock.describeTasks(any(DescribeTasksRequest.class)))
                .thenReturn(buildDescribeTasksResponse());

        testAWS.makeRequests(ecsClientMock);

        // Verificar as chamadas ao MockServer
        mockServer.verify(request().withMethod("GET").withPath(MOCK_ENDPOINT).withHeader("ACCOUNT", "valor_do_header"),
                org.mockserver.verify.VerificationTimes.exactly(1));
    }

    private static DescribeTasksResponse buildDescribeTasksResponse() {
        Task task1 = Task.builder()
                .taskDefinitionArn(TASK_DEFINITION_ARN)
                .containers(Container.builder()
                        .networkInterfaces(NetworkInterface.builder()
                                .privateIpv4Address("localhost:"+mockServer.getPort())
                                .build())
                        .build())
                .build();

        Task task2 = Task.builder()
                .taskDefinitionArn("anotherArn")
                .containers(Container.builder()
                        .networkInterfaces(NetworkInterface.builder()
                                .privateIpv4Address("192.168.0.2")
                                .build())
                        .build())
                .build();

        return DescribeTasksResponse.builder()
                .tasks(task1, task2)
                .build();
    }

}
