/**
 * Deribit API
 * #Overview  Deribit provides three different interfaces to access the API:  * [JSON-RPC over Websocket](#json-rpc) * [JSON-RPC over HTTP](#json-rpc) * [FIX](#fix-api) (Financial Information eXchange)  With the API Console you can use and test the JSON-RPC API, both via HTTP and  via Websocket. To visit the API console, go to __Account > API tab >  API Console tab.__   ##Naming Deribit tradeable assets or instruments use the following system of naming:  |Kind|Examples|Template|Comments| |----|--------|--------|--------| |Future|<code>BTC-25MAR16</code>, <code>BTC-5AUG16</code>|<code>BTC-DMMMYY</code>|<code>BTC</code> is currency, <code>DMMMYY</code> is expiration date, <code>D</code> stands for day of month (1 or 2 digits), <code>MMM</code> - month (3 first letters in English), <code>YY</code> stands for year.| |Perpetual|<code>BTC-PERPETUAL</code>                        ||Perpetual contract for currency <code>BTC</code>.| |Option|<code>BTC-25MAR16-420-C</code>, <code>BTC-5AUG16-580-P</code>|<code>BTC-DMMMYY-STRIKE-K</code>|<code>STRIKE</code> is option strike price in USD. Template <code>K</code> is option kind: <code>C</code> for call options or <code>P</code> for put options.|   # JSON-RPC JSON-RPC is a light-weight remote procedure call (RPC) protocol. The  [JSON-RPC specification](https://www.jsonrpc.org/specification) defines the data structures that are used for the messages that are exchanged between client and server, as well as the rules around their processing. JSON-RPC uses JSON (RFC 4627) as data format.  JSON-RPC is transport agnostic: it does not specify which transport mechanism must be used. The Deribit API supports both Websocket (preferred) and HTTP (with limitations: subscriptions are not supported over HTTP).  ## Request messages > An example of a request message:  ```json {     \"jsonrpc\": \"2.0\",     \"id\": 8066,     \"method\": \"public/ticker\",     \"params\": {         \"instrument\": \"BTC-24AUG18-6500-P\"     } } ```  According to the JSON-RPC sepcification the requests must be JSON objects with the following fields.  |Name|Type|Description| |----|----|-----------| |jsonrpc|string|The version of the JSON-RPC spec: \"2.0\"| |id|integer or string|An identifier of the request. If it is included, then the response will contain the same identifier| |method|string|The method to be invoked| |params|object|The parameters values for the method. The field names must match with the expected parameter names. The parameters that are expected are described in the documentation for the methods, below.|  <aside class=\"warning\"> The JSON-RPC specification describes two features that are currently not supported by the API:  <ul> <li>Specification of parameter values by position</li> <li>Batch requests</li> </ul>  </aside>   ## Response messages > An example of a response message:  ```json {     \"jsonrpc\": \"2.0\",     \"id\": 5239,     \"testnet\": false,     \"result\": [         {             \"currency\": \"BTC\",             \"currencyLong\": \"Bitcoin\",             \"minConfirmation\": 2,             \"txFee\": 0.0006,             \"isActive\": true,             \"coinType\": \"BITCOIN\",             \"baseAddress\": null         }     ],     \"usIn\": 1535043730126248,     \"usOut\": 1535043730126250,     \"usDiff\": 2 } ```  The JSON-RPC API always responds with a JSON object with the following fields.   |Name|Type|Description| |----|----|-----------| |id|integer|This is the same id that was sent in the request.| |result|any|If successful, the result of the API call. The format for the result is described with each method.| |error|error object|Only present if there was an error invoking the method. The error object is described below.| |testnet|boolean|Indicates whether the API in use is actually the test API.  <code>false</code> for production server, <code>true</code> for test server.| |usIn|integer|The timestamp when the requests was received (microseconds since the Unix epoch)| |usOut|integer|The timestamp when the response was sent (microseconds since the Unix epoch)| |usDiff|integer|The number of microseconds that was spent handling the request|  <aside class=\"notice\"> The fields <code>testnet</code>, <code>usIn</code>, <code>usOut</code> and <code>usDiff</code> are not part of the JSON-RPC standard.  <p>In order not to clutter the examples they will generally be omitted from the example code.</p> </aside>  > An example of a response with an error:  ```json {     \"jsonrpc\": \"2.0\",     \"id\": 8163,     \"error\": {         \"code\": 11050,         \"message\": \"bad_request\"     },     \"testnet\": false,     \"usIn\": 1535037392434763,     \"usOut\": 1535037392448119,     \"usDiff\": 13356 } ``` In case of an error the response message will contain the error field, with as value an object with the following with the following fields:  |Name|Type|Description |----|----|-----------| |code|integer|A number that indicates the kind of error.| |message|string|A short description that indicates the kind of error.| |data|any|Additional data about the error. This field may be omitted.|  ## Notifications  > An example of a notification:  ```json {     \"jsonrpc\": \"2.0\",     \"method\": \"subscription\",     \"params\": {         \"channel\": \"deribit_price_index.btc_usd\",         \"data\": {             \"timestamp\": 1535098298227,             \"price\": 6521.17,             \"index_name\": \"btc_usd\"         }     } } ```  API users can subscribe to certain types of notifications. This means that they will receive JSON-RPC notification-messages from the server when certain events occur, such as changes to the index price or changes to the order book for a certain instrument.   The API methods [public/subscribe](#public-subscribe) and [private/subscribe](#private-subscribe) are used to set up a subscription. Since HTTP does not support the sending of messages from server to client, these methods are only availble when using the Websocket transport mechanism.  At the moment of subscription a \"channel\" must be specified. The channel determines the type of events that will be received.  See [Subscriptions](#subscriptions) for more details about the channels.  In accordance with the JSON-RPC specification, the format of a notification  is that of a request message without an <code>id</code> field. The value of the <code>method</code> field will always be <code>\"subscription\"</code>. The <code>params</code> field will always be an object with 2 members: <code>channel</code> and <code>data</code>. The value of the <code>channel</code> member is the name of the channel (a string). The value of the <code>data</code> member is an object that contains data  that is specific for the channel.   ## Authentication  > An example of a JSON request with token:  ```json {     \"id\": 5647,     \"method\": \"private/get_subaccounts\",     \"params\": {         \"access_token\": \"67SVutDoVZSzkUStHSuk51WntMNBJ5mh5DYZhwzpiqDF\"     } } ```  The API consists of `public` and `private` methods. The public methods do not require authentication. The private methods use OAuth 2.0 authentication. This means that a valid OAuth access token must be included in the request, which can get achived by calling method [public/auth](#public-auth).  When the token was assigned to the user, it should be passed along, with other request parameters, back to the server:  |Connection type|Access token placement |----|-----------| |**Websocket**|Inside request JSON parameters, as an `access_token` field| |**HTTP (REST)**|Header `Authorization: bearer ```Token``` ` value|  ### Additional authorization method - basic user credentials  <span style=\"color:red\"><b> ! Not recommended - however, it could be useful for quick testing API</b></span></br>  Every `private` method could be accessed by providing, inside HTTP `Authorization: Basic XXX` header, values with user `ClientId` and assigned `ClientSecret` (both values can be found on the API page on the Deribit website) encoded with `Base64`:  <code>Authorization: Basic BASE64(`ClientId` + `:` + `ClientSecret`)</code>   ### Additional authorization method - Deribit signature credentials  The Derbit service provides dedicated authorization method, which harness user generated signature to increase security level for passing request data. Generated value is passed inside `Authorization` header, coded as:  <code>Authorization: deri-hmac-sha256 id=```ClientId```,ts=```Timestamp```,sig=```Signature```,nonce=```Nonce```</code>  where:  |Deribit credential|Description |----|-----------| |*ClientId*|Can be found on the API page on the Deribit website| |*Timestamp*|Time when the request was generated - given as **miliseconds**. It's valid for **60 seconds** since generation, after that time any request with an old timestamp will be rejected.| |*Signature*|Value for signature calculated as described below | |*Nonce*|Single usage, user generated initialization vector for the server token|  The signature is generated by the following formula:  <code> Signature = HEX_STRING( HMAC-SHA256( ClientSecret, StringToSign ) );</code></br>  <code> StringToSign =  Timestamp + \"\\n\" + Nonce + \"\\n\" + RequestData;</code></br>  <code> RequestData =  UPPERCASE(HTTP_METHOD())  + \"\\n\" + URI() + \"\\n\" + RequestBody + \"\\n\";</code></br>   e.g. (using shell with ```openssl``` tool):  <code>&nbsp;&nbsp;&nbsp;&nbsp;ClientId=AAAAAAAAAAA</code></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;ClientSecret=ABCD</code></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;Timestamp=$( date +%s000 )</code></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;Nonce=$( cat /dev/urandom | tr -dc 'a-z0-9' | head -c8 )</code></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;URI=\"/api/v2/private/get_account_summary?currency=BTC\"</code></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;HttpMethod=GET</code></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;Body=\"\"</code></br></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;Signature=$( echo -ne \"${Timestamp}\\n${Nonce}\\n${HttpMethod}\\n${URI}\\n${Body}\\n\" | openssl sha256 -r -hmac \"$ClientSecret\" | cut -f1 -d' ' )</code></br></br> <code>&nbsp;&nbsp;&nbsp;&nbsp;echo $Signature</code></br></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;shell output> ea40d5e5e4fae235ab22b61da98121fbf4acdc06db03d632e23c66bcccb90d2c  (**WARNING**: Exact value depends on current timestamp and client credentials</code></br></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;curl -s -X ${HttpMethod} -H \"Authorization: deri-hmac-sha256 id=${ClientId},ts=${Timestamp},nonce=${Nonce},sig=${Signature}\" \"https://www.deribit.com${URI}\"</code></br></br>    ### Additional authorization method - signature credentials (WebSocket API)  When connecting through Websocket, user can request for authorization using ```client_credential``` method, which requires providing following parameters (as a part of JSON request):  |JSON parameter|Description |----|-----------| |*grant_type*|Must be **client_signature**| |*client_id*|Can be found on the API page on the Deribit website| |*timestamp*|Time when the request was generated - given as **miliseconds**. It's valid for **60 seconds** since generation, after that time any request with an old timestamp will be rejected.| |*signature*|Value for signature calculated as described below | |*nonce*|Single usage, user generated initialization vector for the server token| |*data*|**Optional** field, which contains any user specific value|  The signature is generated by the following formula:  <code> StringToSign =  Timestamp + \"\\n\" + Nonce + \"\\n\" + Data;</code></br>  <code> Signature = HEX_STRING( HMAC-SHA256( ClientSecret, StringToSign ) );</code></br>   e.g. (using shell with ```openssl``` tool):  <code>&nbsp;&nbsp;&nbsp;&nbsp;ClientId=AAAAAAAAAAA</code></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;ClientSecret=ABCD</code></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;Timestamp=$( date +%s000 ) # e.g. 1554883365000 </code></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;Nonce=$( cat /dev/urandom | tr -dc 'a-z0-9' | head -c8 ) # e.g. fdbmmz79 </code></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;Data=\"\"</code></br></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;Signature=$( echo -ne \"${Timestamp}\\n${Nonce}\\n${Data}\\n\" | openssl sha256 -r -hmac \"$ClientSecret\" | cut -f1 -d' ' )</code></br></br> <code>&nbsp;&nbsp;&nbsp;&nbsp;echo $Signature</code></br></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;shell output> e20c9cd5639d41f8bbc88f4d699c4baf94a4f0ee320e9a116b72743c449eb994  (**WARNING**: Exact value depends on current timestamp and client credentials</code></br></br>   You can also check the signature value using some online tools like, e.g: [https://codebeautify.org/hmac-generator](https://codebeautify.org/hmac-generator) (but don't forget about adding *newline* after each part of the hashed text and remember that you **should use** it only with your **test credentials**).   Here's a sample JSON request created using the values from the example above:  <code> {                            </br> &nbsp;&nbsp;\"jsonrpc\" : \"2.0\",         </br> &nbsp;&nbsp;\"id\" : 9929,               </br> &nbsp;&nbsp;\"method\" : \"public/auth\",  </br> &nbsp;&nbsp;\"params\" :                 </br> &nbsp;&nbsp;{                        </br> &nbsp;&nbsp;&nbsp;&nbsp;\"grant_type\" : \"client_signature\",   </br> &nbsp;&nbsp;&nbsp;&nbsp;\"client_id\" : \"AAAAAAAAAAA\",         </br> &nbsp;&nbsp;&nbsp;&nbsp;\"timestamp\": \"1554883365000\",        </br> &nbsp;&nbsp;&nbsp;&nbsp;\"nonce\": \"fdbmmz79\",                 </br> &nbsp;&nbsp;&nbsp;&nbsp;\"data\": \"\",                          </br> &nbsp;&nbsp;&nbsp;&nbsp;\"signature\" : \"e20c9cd5639d41f8bbc88f4d699c4baf94a4f0ee320e9a116b72743c449eb994\"  </br> &nbsp;&nbsp;}                        </br> }                            </br> </code>   ### Access scope  When asking for `access token` user can provide the required access level (called `scope`) which defines what type of functionality he/she wants to use, and whether requests are only going to check for some data or also to update them.  Scopes are required and checked for `private` methods, so if you plan to use only `public` information you can stay with values assigned by default.  |Scope|Description |----|-----------| |*account:read*|Access to **account** methods - read only data| |*account:read_write*|Access to **account** methods - allows to manage account settings, add subaccounts, etc.| |*trade:read*|Access to **trade** methods - read only data| |*trade:read_write*|Access to **trade** methods - required to create and modify orders| |*wallet:read*|Access to **wallet** methods - read only data| |*wallet:read_write*|Access to **wallet** methods - allows to withdraw, generate new deposit address, etc.| |*wallet:none*, *account:none*, *trade:none*|Blocked access to specified functionality|    <span style=\"color:red\">**NOTICE:**</span> Depending on choosing an authentication method (```grant type```) some scopes could be narrowed by the server. e.g. when ```grant_type = client_credentials``` and ```scope = wallet:read_write``` it's modified by the server as ```scope = wallet:read```\"   ## JSON-RPC over websocket Websocket is the prefered transport mechanism for the JSON-RPC API, because it is faster and because it can support [subscriptions](#subscriptions) and [cancel on disconnect](#private-enable_cancel_on_disconnect). The code examples that can be found next to each of the methods show how websockets can be used from Python or Javascript/node.js.  ## JSON-RPC over HTTP Besides websockets it is also possible to use the API via HTTP. The code examples for 'shell' show how this can be done using curl. Note that subscriptions and cancel on disconnect are not supported via HTTP.  #Methods 
 *
 * The version of the OpenAPI document: 2.0.0
 *
 * NOTE: This class is auto generated by OpenAPI-Generator 4.0.2-SNAPSHOT.
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

/*
 * Order.h
 *
 * 
 */

#ifndef ORG_OPENAPITOOLS_CLIENT_MODEL_Order_H_
#define ORG_OPENAPITOOLS_CLIENT_MODEL_Order_H_


#include "../ModelBase.h"

#include <cpprest/details/basic_types.h>

namespace org {
namespace openapitools {
namespace client {
namespace model {


/// <summary>
/// 
/// </summary>
class  Order
    : public ModelBase
{
public:
    Order();
    virtual ~Order();

    /////////////////////////////////////////////
    /// ModelBase overrides

    void validate() override;

    web::json::value toJson() const override;
    void fromJson(const web::json::value& json) override;

    void toMultipart(std::shared_ptr<MultipartFormData> multipart, const utility::string_t& namePrefix) const override;
    void fromMultiPart(std::shared_ptr<MultipartFormData> multipart, const utility::string_t& namePrefix) override;

    /////////////////////////////////////////////
    /// Order members

    /// <summary>
    /// direction, &#x60;buy&#x60; or &#x60;sell&#x60;
    /// </summary>
    utility::string_t getDirection() const;
    
    void setDirection(const utility::string_t& value);

    /// <summary>
    /// &#x60;true&#x60; for reduce-only orders only
    /// </summary>
    bool isReduceOnly() const;
    bool reduceOnlyIsSet() const;
    void unsetReduce_only();

    void setReduceOnly(bool value);

    /// <summary>
    /// Whether the stop order has been triggered (Only for stop orders)
    /// </summary>
    bool isTriggered() const;
    bool triggeredIsSet() const;
    void unsetTriggered();

    void setTriggered(bool value);

    /// <summary>
    /// Unique order identifier
    /// </summary>
    utility::string_t getOrderId() const;
    
    void setOrderId(const utility::string_t& value);

    /// <summary>
    /// Price in base currency
    /// </summary>
    double getPrice() const;
    
    void setPrice(double value);

    /// <summary>
    /// Order time in force: &#x60;\&quot;good_til_cancelled\&quot;&#x60;, &#x60;\&quot;fill_or_kill\&quot;&#x60;, &#x60;\&quot;immediate_or_cancel\&quot;&#x60;
    /// </summary>
    utility::string_t getTimeInForce() const;
    
    void setTimeInForce(const utility::string_t& value);

    /// <summary>
    /// &#x60;true&#x60; if created with API
    /// </summary>
    bool isApi() const;
    
    void setApi(bool value);

    /// <summary>
    /// order state, &#x60;\&quot;open\&quot;&#x60;, &#x60;\&quot;filled\&quot;&#x60;, &#x60;\&quot;rejected\&quot;&#x60;, &#x60;\&quot;cancelled\&quot;&#x60;, &#x60;\&quot;untriggered\&quot;&#x60;
    /// </summary>
    utility::string_t getOrderState() const;
    
    void setOrderState(const utility::string_t& value);

    /// <summary>
    /// Implied volatility in percent. (Only if &#x60;advanced&#x3D;\&quot;implv\&quot;&#x60;)
    /// </summary>
    double getImplv() const;
    bool implvIsSet() const;
    void unsetImplv();

    void setImplv(double value);

    /// <summary>
    /// advanced type: &#x60;\&quot;usd\&quot;&#x60; or &#x60;\&quot;implv\&quot;&#x60; (Only for options; field is omitted if not applicable). 
    /// </summary>
    utility::string_t getAdvanced() const;
    bool advancedIsSet() const;
    void unsetAdvanced();

    void setAdvanced(const utility::string_t& value);

    /// <summary>
    /// &#x60;true&#x60; for post-only orders only
    /// </summary>
    bool isPostOnly() const;
    
    void setPostOnly(bool value);

    /// <summary>
    /// Option price in USD (Only if &#x60;advanced&#x3D;\&quot;usd\&quot;&#x60;)
    /// </summary>
    double getUsd() const;
    bool usdIsSet() const;
    void unsetUsd();

    void setUsd(double value);

    /// <summary>
    /// stop price (Only for future stop orders)
    /// </summary>
    double getStopPrice() const;
    bool stopPriceIsSet() const;
    void unsetStop_price();

    void setStopPrice(double value);

    /// <summary>
    /// order type, &#x60;\&quot;limit\&quot;&#x60;, &#x60;\&quot;market\&quot;&#x60;, &#x60;\&quot;stop_limit\&quot;&#x60;, &#x60;\&quot;stop_market\&quot;&#x60;
    /// </summary>
    utility::string_t getOrderType() const;
    
    void setOrderType(const utility::string_t& value);

    /// <summary>
    /// The timestamp (seconds since the Unix epoch, with millisecond precision)
    /// </summary>
    int32_t getLastUpdateTimestamp() const;
    
    void setLastUpdateTimestamp(int32_t value);

    /// <summary>
    /// Original order type. Optional field
    /// </summary>
    utility::string_t getOriginalOrderType() const;
    bool originalOrderTypeIsSet() const;
    void unsetOriginal_order_type();

    void setOriginalOrderType(const utility::string_t& value);

    /// <summary>
    /// Maximum amount within an order to be shown to other traders, 0 for invisible order.
    /// </summary>
    double getMaxShow() const;
    
    void setMaxShow(double value);

    /// <summary>
    /// Profit and loss in base currency.
    /// </summary>
    double getProfitLoss() const;
    bool profitLossIsSet() const;
    void unsetProfit_loss();

    void setProfitLoss(double value);

    /// <summary>
    /// &#x60;true&#x60; if order was automatically created during liquidation
    /// </summary>
    bool isIsLiquidation() const;
    
    void setIsLiquidation(bool value);

    /// <summary>
    /// Filled amount of the order. For perpetual and futures the filled_amount is in USD units, for options - in units or corresponding cryptocurrency contracts, e.g., BTC or ETH.
    /// </summary>
    double getFilledAmount() const;
    bool filledAmountIsSet() const;
    void unsetFilled_amount();

    void setFilledAmount(double value);

    /// <summary>
    /// user defined label (up to 32 characters)
    /// </summary>
    utility::string_t getLabel() const;
    
    void setLabel(const utility::string_t& value);

    /// <summary>
    /// Commission paid so far (in base currency)
    /// </summary>
    double getCommission() const;
    bool commissionIsSet() const;
    void unsetCommission();

    void setCommission(double value);

    /// <summary>
    /// It represents the requested order size. For perpetual and futures the amount is in USD units, for options it is amount of corresponding cryptocurrency contracts, e.g., BTC or ETH.
    /// </summary>
    double getAmount() const;
    bool amountIsSet() const;
    void unsetAmount();

    void setAmount(double value);

    /// <summary>
    /// Trigger type (Only for stop orders). Allowed values: &#x60;\&quot;index_price\&quot;&#x60;, &#x60;\&quot;mark_price\&quot;&#x60;, &#x60;\&quot;last_price\&quot;&#x60;.
    /// </summary>
    utility::string_t getTrigger() const;
    bool triggerIsSet() const;
    void unsetTrigger();

    void setTrigger(const utility::string_t& value);

    /// <summary>
    /// Unique instrument identifier
    /// </summary>
    utility::string_t getInstrumentName() const;
    bool instrumentNameIsSet() const;
    void unsetInstrument_name();

    void setInstrumentName(const utility::string_t& value);

    /// <summary>
    /// The timestamp (seconds since the Unix epoch, with millisecond precision)
    /// </summary>
    int32_t getCreationTimestamp() const;
    
    void setCreationTimestamp(int32_t value);

    /// <summary>
    /// Average fill price of the order
    /// </summary>
    double getAveragePrice() const;
    bool averagePriceIsSet() const;
    void unsetAverage_price();

    void setAveragePrice(double value);


protected:
    utility::string_t m_Direction;
        bool m_Reduce_only;
    bool m_Reduce_onlyIsSet;
    bool m_Triggered;
    bool m_TriggeredIsSet;
    utility::string_t m_Order_id;
        double m_Price;
        utility::string_t m_Time_in_force;
        bool m_Api;
        utility::string_t m_Order_state;
        double m_Implv;
    bool m_ImplvIsSet;
    utility::string_t m_Advanced;
    bool m_AdvancedIsSet;
    bool m_Post_only;
        double m_Usd;
    bool m_UsdIsSet;
    double m_Stop_price;
    bool m_Stop_priceIsSet;
    utility::string_t m_Order_type;
        int32_t m_Last_update_timestamp;
        utility::string_t m_Original_order_type;
    bool m_Original_order_typeIsSet;
    double m_Max_show;
        double m_Profit_loss;
    bool m_Profit_lossIsSet;
    bool m_Is_liquidation;
        double m_Filled_amount;
    bool m_Filled_amountIsSet;
    utility::string_t m_Label;
        double m_Commission;
    bool m_CommissionIsSet;
    double m_Amount;
    bool m_AmountIsSet;
    utility::string_t m_Trigger;
    bool m_TriggerIsSet;
    utility::string_t m_Instrument_name;
    bool m_Instrument_nameIsSet;
    int32_t m_Creation_timestamp;
        double m_Average_price;
    bool m_Average_priceIsSet;
};


}
}
}
}

#endif /* ORG_OPENAPITOOLS_CLIENT_MODEL_Order_H_ */
