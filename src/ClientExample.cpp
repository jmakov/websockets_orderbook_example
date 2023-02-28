#include <chrono>
#include <iostream>
#include <thread>
#include <vector>

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
            std::cout << msg << std::endl;
        };
        onclose = []() {std::cout << "connection closed" << std::endl;};

        open(url.data());
    };
    void subscribe(std::vector<std::string>& instruments) {
        rapidjson::StringBuffer buffer;
        rapidjson::Writer<rapidjson::StringBuffer> writer(buffer);

        writer.StartObject();
        writer.Key("jsonrpc");
        writer.String("2.0");
        writer.Key("id");
        writer.Int(m_request_id);
        writer.Key("method");
        writer.String("public/subscribe");
        writer.Key("params");
        writer.StartObject();
        writer.Key("channels");

        writer.StartArray();
        for(const auto& instrument: instruments) {
            writer.String(instrument.c_str());
        }
        writer.EndArray();

        writer.EndObject();
        writer.EndObject();

        while(!isConnected()) {
            std::this_thread::sleep_for(std::chrono::seconds(1));
        }
        send(buffer.GetString());
        ++m_request_id;
    }
private:
    int m_request_id {0};
};

int main() {
    WSClient client{};
    client.connect("wss://test.deribit.com/ws/api/v2");
    std::vector<std::string> instruments_to_subscribe_to = {
            "book.ADA_USDC-PERPETUAL.none.20.100ms",
            "book.BTC-PERPETUAL.none.20.100ms"};
    std::vector<std::string> trades_to_subscribe_to = {
            "trades.ADA_USDC-PERPETUAL.100ms",
            "trades.BTC-PERPETUAL.100ms"};

    client.subscribe(instruments_to_subscribe_to);
    client.subscribe(trades_to_subscribe_to);

    std::this_thread::sleep_for(std::chrono::seconds(10000));
}