#include <chrono>
#include <iostream>
#include <thread>

#include <hv/WebSocketClient.h>
#include <hv/AsyncHttpClient.h>
#include <rapidjson/writer.h>
#include "rapidjson/stringbuffer.h"


class WSClient final : public hv::WebSocketClient {
public:
    ~WSClient() final {
        if (isConnected()) {
            close();
        }
    }

    void connect(std::string_view url) {
        // TODO: the lib is using function pointers for callbacks. In our benchmarks we see that it's possible to do it
        //  almost 5x faster using CRTP visitor pattern
        onopen = [this]() {
            const HttpResponsePtr& resp = getHttpResponse();
            std::cout << "connection established: " << resp->body.c_str() << std::endl;
        };
        onmessage = [this](const std::string& msg) {
            // TODO: parse market msgs and insert into OB
        };
        onclose = []() {std::cout << "connection closed" << std::endl;};

        open(url.data());
    };
    void authenticate(std::string_view client_id, std::string_view client_secret) {
        rapidjson::StringBuffer buffer;
        rapidjson::Writer<rapidjson::StringBuffer> writer(buffer);

        writer.StartObject();
        writer.Key("jsonrpc");
        writer.String("2.0");
        writer.Key("id");
        writer.Int(3);
        writer.Key("method");
        writer.String("public/auth");
        writer.Key("params");
        writer.StartObject();
        writer.Key("grant_type");
        writer.String("client_credentials");
        writer.Key("client_id");
        writer.String(client_id.data());
        writer.Key("client_secret");
        writer.String(client_secret.data());
        writer.EndObject();
        writer.EndObject();

        while(!isConnected()) {
            std::this_thread::sleep_for(std::chrono::seconds(1));
        }
        // TODO: investigate why we get "invalid_credentials" when the same request works using the API console.
        //  Using `curl` in Bash also doesn't work (copy paste example from Deribit API docs with changed values)...
        send(buffer.GetString());
    }
};

int main() {
    WSClient client{};
    client.connect("wss://test.deribit.com/ws/api/v2");
    client.authenticate("pMyhloos", "mh4XFX9frkpPiKweIs2clGtV5EFqlauRECFjEzOAM5g");

    std::this_thread::sleep_for(std::chrono::seconds(2));
}