/*
 * Deribit API
 * #Overview  Deribit provides three different interfaces to access the API:  * [JSON-RPC over Websocket](#json-rpc) * [JSON-RPC over HTTP](#json-rpc) * [FIX](#fix-api) (Financial Information eXchange)  With the API Console you can use and test the JSON-RPC API, both via HTTP and  via Websocket. To visit the API console, go to __Account > API tab >  API Console tab.__   ##Naming Deribit tradeable assets or instruments use the following system of naming:  |Kind|Examples|Template|Comments| |----|--------|--------|--------| |Future|<code>BTC-25MAR16</code>, <code>BTC-5AUG16</code>|<code>BTC-DMMMYY</code>|<code>BTC</code> is currency, <code>DMMMYY</code> is expiration date, <code>D</code> stands for day of month (1 or 2 digits), <code>MMM</code> - month (3 first letters in English), <code>YY</code> stands for year.| |Perpetual|<code>BTC-PERPETUAL</code>                        ||Perpetual contract for currency <code>BTC</code>.| |Option|<code>BTC-25MAR16-420-C</code>, <code>BTC-5AUG16-580-P</code>|<code>BTC-DMMMYY-STRIKE-K</code>|<code>STRIKE</code> is option strike price in USD. Template <code>K</code> is option kind: <code>C</code> for call options or <code>P</code> for put options.|   # JSON-RPC JSON-RPC is a light-weight remote procedure call (RPC) protocol. The  [JSON-RPC specification](https://www.jsonrpc.org/specification) defines the data structures that are used for the messages that are exchanged between client and server, as well as the rules around their processing. JSON-RPC uses JSON (RFC 4627) as data format.  JSON-RPC is transport agnostic: it does not specify which transport mechanism must be used. The Deribit API supports both Websocket (preferred) and HTTP (with limitations: subscriptions are not supported over HTTP).  ## Request messages > An example of a request message:  ```json {     \"jsonrpc\": \"2.0\",     \"id\": 8066,     \"method\": \"public/ticker\",     \"params\": {         \"instrument\": \"BTC-24AUG18-6500-P\"     } } ```  According to the JSON-RPC sepcification the requests must be JSON objects with the following fields.  |Name|Type|Description| |----|----|-----------| |jsonrpc|string|The version of the JSON-RPC spec: \"2.0\"| |id|integer or string|An identifier of the request. If it is included, then the response will contain the same identifier| |method|string|The method to be invoked| |params|object|The parameters values for the method. The field names must match with the expected parameter names. The parameters that are expected are described in the documentation for the methods, below.|  <aside class=\"warning\"> The JSON-RPC specification describes two features that are currently not supported by the API:  <ul> <li>Specification of parameter values by position</li> <li>Batch requests</li> </ul>  </aside>   ## Response messages > An example of a response message:  ```json {     \"jsonrpc\": \"2.0\",     \"id\": 5239,     \"testnet\": false,     \"result\": [         {             \"currency\": \"BTC\",             \"currencyLong\": \"Bitcoin\",             \"minConfirmation\": 2,             \"txFee\": 0.0006,             \"isActive\": true,             \"coinType\": \"BITCOIN\",             \"baseAddress\": null         }     ],     \"usIn\": 1535043730126248,     \"usOut\": 1535043730126250,     \"usDiff\": 2 } ```  The JSON-RPC API always responds with a JSON object with the following fields.   |Name|Type|Description| |----|----|-----------| |id|integer|This is the same id that was sent in the request.| |result|any|If successful, the result of the API call. The format for the result is described with each method.| |error|error object|Only present if there was an error invoking the method. The error object is described below.| |testnet|boolean|Indicates whether the API in use is actually the test API.  <code>false</code> for production server, <code>true</code> for test server.| |usIn|integer|The timestamp when the requests was received (microseconds since the Unix epoch)| |usOut|integer|The timestamp when the response was sent (microseconds since the Unix epoch)| |usDiff|integer|The number of microseconds that was spent handling the request|  <aside class=\"notice\"> The fields <code>testnet</code>, <code>usIn</code>, <code>usOut</code> and <code>usDiff</code> are not part of the JSON-RPC standard.  <p>In order not to clutter the examples they will generally be omitted from the example code.</p> </aside>  > An example of a response with an error:  ```json {     \"jsonrpc\": \"2.0\",     \"id\": 8163,     \"error\": {         \"code\": 11050,         \"message\": \"bad_request\"     },     \"testnet\": false,     \"usIn\": 1535037392434763,     \"usOut\": 1535037392448119,     \"usDiff\": 13356 } ``` In case of an error the response message will contain the error field, with as value an object with the following with the following fields:  |Name|Type|Description |----|----|-----------| |code|integer|A number that indicates the kind of error.| |message|string|A short description that indicates the kind of error.| |data|any|Additional data about the error. This field may be omitted.|  ## Notifications  > An example of a notification:  ```json {     \"jsonrpc\": \"2.0\",     \"method\": \"subscription\",     \"params\": {         \"channel\": \"deribit_price_index.btc_usd\",         \"data\": {             \"timestamp\": 1535098298227,             \"price\": 6521.17,             \"index_name\": \"btc_usd\"         }     } } ```  API users can subscribe to certain types of notifications. This means that they will receive JSON-RPC notification-messages from the server when certain events occur, such as changes to the index price or changes to the order book for a certain instrument.   The API methods [public/subscribe](#public-subscribe) and [private/subscribe](#private-subscribe) are used to set up a subscription. Since HTTP does not support the sending of messages from server to client, these methods are only availble when using the Websocket transport mechanism.  At the moment of subscription a \"channel\" must be specified. The channel determines the type of events that will be received.  See [Subscriptions](#subscriptions) for more details about the channels.  In accordance with the JSON-RPC specification, the format of a notification  is that of a request message without an <code>id</code> field. The value of the <code>method</code> field will always be <code>\"subscription\"</code>. The <code>params</code> field will always be an object with 2 members: <code>channel</code> and <code>data</code>. The value of the <code>channel</code> member is the name of the channel (a string). The value of the <code>data</code> member is an object that contains data  that is specific for the channel.   ## Authentication  > An example of a JSON request with token:  ```json {     \"id\": 5647,     \"method\": \"private/get_subaccounts\",     \"params\": {         \"access_token\": \"67SVutDoVZSzkUStHSuk51WntMNBJ5mh5DYZhwzpiqDF\"     } } ```  The API consists of `public` and `private` methods. The public methods do not require authentication. The private methods use OAuth 2.0 authentication. This means that a valid OAuth access token must be included in the request, which can get achived by calling method [public/auth](#public-auth).  When the token was assigned to the user, it should be passed along, with other request parameters, back to the server:  |Connection type|Access token placement |----|-----------| |**Websocket**|Inside request JSON parameters, as an `access_token` field| |**HTTP (REST)**|Header `Authorization: bearer ```Token``` ` value|  ### Additional authorization method - basic user credentials  <span style=\"color:red\"><b> ! Not recommended - however, it could be useful for quick testing API</b></span></br>  Every `private` method could be accessed by providing, inside HTTP `Authorization: Basic XXX` header, values with user `ClientId` and assigned `ClientSecret` (both values can be found on the API page on the Deribit website) encoded with `Base64`:  <code>Authorization: Basic BASE64(`ClientId` + `:` + `ClientSecret`)</code>   ### Additional authorization method - Deribit signature credentials  The Derbit service provides dedicated authorization method, which harness user generated signature to increase security level for passing request data. Generated value is passed inside `Authorization` header, coded as:  <code>Authorization: deri-hmac-sha256 id=```ClientId```,ts=```Timestamp```,sig=```Signature```,nonce=```Nonce```</code>  where:  |Deribit credential|Description |----|-----------| |*ClientId*|Can be found on the API page on the Deribit website| |*Timestamp*|Time when the request was generated - given as **miliseconds**. It's valid for **60 seconds** since generation, after that time any request with an old timestamp will be rejected.| |*Signature*|Value for signature calculated as described below | |*Nonce*|Single usage, user generated initialization vector for the server token|  The signature is generated by the following formula:  <code> Signature = HEX_STRING( HMAC-SHA256( ClientSecret, StringToSign ) );</code></br>  <code> StringToSign =  Timestamp + \"\\n\" + Nonce + \"\\n\" + RequestData;</code></br>  <code> RequestData =  UPPERCASE(HTTP_METHOD())  + \"\\n\" + URI() + \"\\n\" + RequestBody + \"\\n\";</code></br>   e.g. (using shell with ```openssl``` tool):  <code>&nbsp;&nbsp;&nbsp;&nbsp;ClientId=AAAAAAAAAAA</code></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;ClientSecret=ABCD</code></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;Timestamp=$( date +%s000 )</code></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;Nonce=$( cat /dev/urandom | tr -dc 'a-z0-9' | head -c8 )</code></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;URI=\"/api/v2/private/get_account_summary?currency=BTC\"</code></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;HttpMethod=GET</code></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;Body=\"\"</code></br></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;Signature=$( echo -ne \"${Timestamp}\\n${Nonce}\\n${HttpMethod}\\n${URI}\\n${Body}\\n\" | openssl sha256 -r -hmac \"$ClientSecret\" | cut -f1 -d' ' )</code></br></br> <code>&nbsp;&nbsp;&nbsp;&nbsp;echo $Signature</code></br></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;shell output> ea40d5e5e4fae235ab22b61da98121fbf4acdc06db03d632e23c66bcccb90d2c  (**WARNING**: Exact value depends on current timestamp and client credentials</code></br></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;curl -s -X ${HttpMethod} -H \"Authorization: deri-hmac-sha256 id=${ClientId},ts=${Timestamp},nonce=${Nonce},sig=${Signature}\" \"https://www.deribit.com${URI}\"</code></br></br>    ### Additional authorization method - signature credentials (WebSocket API)  When connecting through Websocket, user can request for authorization using ```client_credential``` method, which requires providing following parameters (as a part of JSON request):  |JSON parameter|Description |----|-----------| |*grant_type*|Must be **client_signature**| |*client_id*|Can be found on the API page on the Deribit website| |*timestamp*|Time when the request was generated - given as **miliseconds**. It's valid for **60 seconds** since generation, after that time any request with an old timestamp will be rejected.| |*signature*|Value for signature calculated as described below | |*nonce*|Single usage, user generated initialization vector for the server token| |*data*|**Optional** field, which contains any user specific value|  The signature is generated by the following formula:  <code> StringToSign =  Timestamp + \"\\n\" + Nonce + \"\\n\" + Data;</code></br>  <code> Signature = HEX_STRING( HMAC-SHA256( ClientSecret, StringToSign ) );</code></br>   e.g. (using shell with ```openssl``` tool):  <code>&nbsp;&nbsp;&nbsp;&nbsp;ClientId=AAAAAAAAAAA</code></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;ClientSecret=ABCD</code></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;Timestamp=$( date +%s000 ) # e.g. 1554883365000 </code></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;Nonce=$( cat /dev/urandom | tr -dc 'a-z0-9' | head -c8 ) # e.g. fdbmmz79 </code></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;Data=\"\"</code></br></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;Signature=$( echo -ne \"${Timestamp}\\n${Nonce}\\n${Data}\\n\" | openssl sha256 -r -hmac \"$ClientSecret\" | cut -f1 -d' ' )</code></br></br> <code>&nbsp;&nbsp;&nbsp;&nbsp;echo $Signature</code></br></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;shell output> e20c9cd5639d41f8bbc88f4d699c4baf94a4f0ee320e9a116b72743c449eb994  (**WARNING**: Exact value depends on current timestamp and client credentials</code></br></br>   You can also check the signature value using some online tools like, e.g: [https://codebeautify.org/hmac-generator](https://codebeautify.org/hmac-generator) (but don't forget about adding *newline* after each part of the hashed text and remember that you **should use** it only with your **test credentials**).   Here's a sample JSON request created using the values from the example above:  <code> {                            </br> &nbsp;&nbsp;\"jsonrpc\" : \"2.0\",         </br> &nbsp;&nbsp;\"id\" : 9929,               </br> &nbsp;&nbsp;\"method\" : \"public/auth\",  </br> &nbsp;&nbsp;\"params\" :                 </br> &nbsp;&nbsp;{                        </br> &nbsp;&nbsp;&nbsp;&nbsp;\"grant_type\" : \"client_signature\",   </br> &nbsp;&nbsp;&nbsp;&nbsp;\"client_id\" : \"AAAAAAAAAAA\",         </br> &nbsp;&nbsp;&nbsp;&nbsp;\"timestamp\": \"1554883365000\",        </br> &nbsp;&nbsp;&nbsp;&nbsp;\"nonce\": \"fdbmmz79\",                 </br> &nbsp;&nbsp;&nbsp;&nbsp;\"data\": \"\",                          </br> &nbsp;&nbsp;&nbsp;&nbsp;\"signature\" : \"e20c9cd5639d41f8bbc88f4d699c4baf94a4f0ee320e9a116b72743c449eb994\"  </br> &nbsp;&nbsp;}                        </br> }                            </br> </code>   ### Access scope  When asking for `access token` user can provide the required access level (called `scope`) which defines what type of functionality he/she wants to use, and whether requests are only going to check for some data or also to update them.  Scopes are required and checked for `private` methods, so if you plan to use only `public` information you can stay with values assigned by default.  |Scope|Description |----|-----------| |*account:read*|Access to **account** methods - read only data| |*account:read_write*|Access to **account** methods - allows to manage account settings, add subaccounts, etc.| |*trade:read*|Access to **trade** methods - read only data| |*trade:read_write*|Access to **trade** methods - required to create and modify orders| |*wallet:read*|Access to **wallet** methods - read only data| |*wallet:read_write*|Access to **wallet** methods - allows to withdraw, generate new deposit address, etc.| |*wallet:none*, *account:none*, *trade:none*|Blocked access to specified functionality|    <span style=\"color:red\">**NOTICE:**</span> Depending on choosing an authentication method (```grant type```) some scopes could be narrowed by the server. e.g. when ```grant_type = client_credentials``` and ```scope = wallet:read_write``` it's modified by the server as ```scope = wallet:read```\"   ## JSON-RPC over websocket Websocket is the prefered transport mechanism for the JSON-RPC API, because it is faster and because it can support [subscriptions](#subscriptions) and [cancel on disconnect](#private-enable_cancel_on_disconnect). The code examples that can be found next to each of the methods show how websockets can be used from Python or Javascript/node.js.  ## JSON-RPC over HTTP Besides websockets it is also possible to use the API via HTTP. The code examples for 'shell' show how this can be done using curl. Note that subscriptions and cancel on disconnect are not supported via HTTP.  #Methods 
 *
 * The version of the OpenAPI document: 2.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.openapitools.client.model;

import java.util.Objects;
import java.util.Arrays;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * UserTrade
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2019-06-03T12:41:14.884+02:00[Europe/Paris]")
public class UserTrade {
  /**
   * Trade direction of the taker
   */
  @JsonAdapter(DirectionEnum.Adapter.class)
  public enum DirectionEnum {
    BUY("buy"),
    
    SELL("sell");

    private String value;

    DirectionEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static DirectionEnum fromValue(String value) {
      for (DirectionEnum b : DirectionEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    public static class Adapter extends TypeAdapter<DirectionEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final DirectionEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public DirectionEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return DirectionEnum.fromValue(value);
      }
    }
  }

  public static final String SERIALIZED_NAME_DIRECTION = "direction";
  @SerializedName(SERIALIZED_NAME_DIRECTION)
  private DirectionEnum direction;

  /**
   * Currency, i.e &#x60;\&quot;BTC\&quot;&#x60;, &#x60;\&quot;ETH\&quot;&#x60;
   */
  @JsonAdapter(FeeCurrencyEnum.Adapter.class)
  public enum FeeCurrencyEnum {
    BTC("BTC"),
    
    ETH("ETH");

    private String value;

    FeeCurrencyEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static FeeCurrencyEnum fromValue(String value) {
      for (FeeCurrencyEnum b : FeeCurrencyEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    public static class Adapter extends TypeAdapter<FeeCurrencyEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final FeeCurrencyEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public FeeCurrencyEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return FeeCurrencyEnum.fromValue(value);
      }
    }
  }

  public static final String SERIALIZED_NAME_FEE_CURRENCY = "fee_currency";
  @SerializedName(SERIALIZED_NAME_FEE_CURRENCY)
  private FeeCurrencyEnum feeCurrency;

  public static final String SERIALIZED_NAME_ORDER_ID = "order_id";
  @SerializedName(SERIALIZED_NAME_ORDER_ID)
  private String orderId;

  public static final String SERIALIZED_NAME_TIMESTAMP = "timestamp";
  @SerializedName(SERIALIZED_NAME_TIMESTAMP)
  private Integer timestamp;

  public static final String SERIALIZED_NAME_PRICE = "price";
  @SerializedName(SERIALIZED_NAME_PRICE)
  private BigDecimal price;

  public static final String SERIALIZED_NAME_IV = "iv";
  @SerializedName(SERIALIZED_NAME_IV)
  private BigDecimal iv;

  public static final String SERIALIZED_NAME_TRADE_ID = "trade_id";
  @SerializedName(SERIALIZED_NAME_TRADE_ID)
  private String tradeId;

  public static final String SERIALIZED_NAME_FEE = "fee";
  @SerializedName(SERIALIZED_NAME_FEE)
  private BigDecimal fee;

  /**
   * Order type: &#x60;\&quot;limit&#x60;, &#x60;\&quot;market\&quot;&#x60;, or &#x60;\&quot;liquidation\&quot;&#x60;
   */
  @JsonAdapter(OrderTypeEnum.Adapter.class)
  public enum OrderTypeEnum {
    LIMIT("limit"),
    
    MARKET("market"),
    
    LIQUIDATION("liquidation");

    private String value;

    OrderTypeEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static OrderTypeEnum fromValue(String value) {
      for (OrderTypeEnum b : OrderTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    public static class Adapter extends TypeAdapter<OrderTypeEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final OrderTypeEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public OrderTypeEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return OrderTypeEnum.fromValue(value);
      }
    }
  }

  public static final String SERIALIZED_NAME_ORDER_TYPE = "order_type";
  @SerializedName(SERIALIZED_NAME_ORDER_TYPE)
  private OrderTypeEnum orderType;

  public static final String SERIALIZED_NAME_TRADE_SEQ = "trade_seq";
  @SerializedName(SERIALIZED_NAME_TRADE_SEQ)
  private Integer tradeSeq;

  public static final String SERIALIZED_NAME_SELF_TRADE = "self_trade";
  @SerializedName(SERIALIZED_NAME_SELF_TRADE)
  private Boolean selfTrade;

  /**
   * order state, &#x60;\&quot;open\&quot;&#x60;, &#x60;\&quot;filled\&quot;&#x60;, &#x60;\&quot;rejected\&quot;&#x60;, &#x60;\&quot;cancelled\&quot;&#x60;, &#x60;\&quot;untriggered\&quot;&#x60; or &#x60;\&quot;archive\&quot;&#x60; (if order was archived)
   */
  @JsonAdapter(StateEnum.Adapter.class)
  public enum StateEnum {
    OPEN("open"),
    
    FILLED("filled"),
    
    REJECTED("rejected"),
    
    CANCELLED("cancelled"),
    
    UNTRIGGERED("untriggered"),
    
    ARCHIVE("archive");

    private String value;

    StateEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static StateEnum fromValue(String value) {
      for (StateEnum b : StateEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    public static class Adapter extends TypeAdapter<StateEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final StateEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public StateEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return StateEnum.fromValue(value);
      }
    }
  }

  public static final String SERIALIZED_NAME_STATE = "state";
  @SerializedName(SERIALIZED_NAME_STATE)
  private StateEnum state;

  public static final String SERIALIZED_NAME_LABEL = "label";
  @SerializedName(SERIALIZED_NAME_LABEL)
  private String label;

  public static final String SERIALIZED_NAME_INDEX_PRICE = "index_price";
  @SerializedName(SERIALIZED_NAME_INDEX_PRICE)
  private BigDecimal indexPrice;

  public static final String SERIALIZED_NAME_AMOUNT = "amount";
  @SerializedName(SERIALIZED_NAME_AMOUNT)
  private BigDecimal amount;

  public static final String SERIALIZED_NAME_INSTRUMENT_NAME = "instrument_name";
  @SerializedName(SERIALIZED_NAME_INSTRUMENT_NAME)
  private String instrumentName;

  /**
   * Direction of the \&quot;tick\&quot; (&#x60;0&#x60; &#x3D; Plus Tick, &#x60;1&#x60; &#x3D; Zero-Plus Tick, &#x60;2&#x60; &#x3D; Minus Tick, &#x60;3&#x60; &#x3D; Zero-Minus Tick).
   */
  @JsonAdapter(TickDirectionEnum.Adapter.class)
  public enum TickDirectionEnum {
    NUMBER_0(0),
    
    NUMBER_1(1),
    
    NUMBER_2(2),
    
    NUMBER_3(3);

    private Integer value;

    TickDirectionEnum(Integer value) {
      this.value = value;
    }

    public Integer getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static TickDirectionEnum fromValue(Integer value) {
      for (TickDirectionEnum b : TickDirectionEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    public static class Adapter extends TypeAdapter<TickDirectionEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final TickDirectionEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public TickDirectionEnum read(final JsonReader jsonReader) throws IOException {
        Integer value = jsonReader.nextInt();
        return TickDirectionEnum.fromValue(value);
      }
    }
  }

  public static final String SERIALIZED_NAME_TICK_DIRECTION = "tick_direction";
  @SerializedName(SERIALIZED_NAME_TICK_DIRECTION)
  private TickDirectionEnum tickDirection;

  public static final String SERIALIZED_NAME_MATCHING_ID = "matching_id";
  @SerializedName(SERIALIZED_NAME_MATCHING_ID)
  private String matchingId;

  /**
   * Describes what was role of users order: &#x60;\&quot;M\&quot;&#x60; when it was maker order, &#x60;\&quot;T\&quot;&#x60; when it was taker order
   */
  @JsonAdapter(LiquidityEnum.Adapter.class)
  public enum LiquidityEnum {
    M("M"),
    
    T("T");

    private String value;

    LiquidityEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static LiquidityEnum fromValue(String value) {
      for (LiquidityEnum b : LiquidityEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    public static class Adapter extends TypeAdapter<LiquidityEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final LiquidityEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public LiquidityEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return LiquidityEnum.fromValue(value);
      }
    }
  }

  public static final String SERIALIZED_NAME_LIQUIDITY = "liquidity";
  @SerializedName(SERIALIZED_NAME_LIQUIDITY)
  private LiquidityEnum liquidity;

  public UserTrade direction(DirectionEnum direction) {
    this.direction = direction;
    return this;
  }

   /**
   * Trade direction of the taker
   * @return direction
  **/
  @ApiModelProperty(required = true, value = "Trade direction of the taker")
  public DirectionEnum getDirection() {
    return direction;
  }

  public void setDirection(DirectionEnum direction) {
    this.direction = direction;
  }

  public UserTrade feeCurrency(FeeCurrencyEnum feeCurrency) {
    this.feeCurrency = feeCurrency;
    return this;
  }

   /**
   * Currency, i.e &#x60;\&quot;BTC\&quot;&#x60;, &#x60;\&quot;ETH\&quot;&#x60;
   * @return feeCurrency
  **/
  @ApiModelProperty(required = true, value = "Currency, i.e `\"BTC\"`, `\"ETH\"`")
  public FeeCurrencyEnum getFeeCurrency() {
    return feeCurrency;
  }

  public void setFeeCurrency(FeeCurrencyEnum feeCurrency) {
    this.feeCurrency = feeCurrency;
  }

  public UserTrade orderId(String orderId) {
    this.orderId = orderId;
    return this;
  }

   /**
   * Id of the user order (maker or taker), i.e. subscriber&#39;s order id that took part in the trade
   * @return orderId
  **/
  @ApiModelProperty(required = true, value = "Id of the user order (maker or taker), i.e. subscriber's order id that took part in the trade")
  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public UserTrade timestamp(Integer timestamp) {
    this.timestamp = timestamp;
    return this;
  }

   /**
   * The timestamp of the trade
   * @return timestamp
  **/
  @ApiModelProperty(example = "1517329113791", required = true, value = "The timestamp of the trade")
  public Integer getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Integer timestamp) {
    this.timestamp = timestamp;
  }

  public UserTrade price(BigDecimal price) {
    this.price = price;
    return this;
  }

   /**
   * The price of the trade
   * @return price
  **/
  @ApiModelProperty(required = true, value = "The price of the trade")
  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public UserTrade iv(BigDecimal iv) {
    this.iv = iv;
    return this;
  }

   /**
   * Option implied volatility for the price (Option only)
   * @return iv
  **/
  @ApiModelProperty(value = "Option implied volatility for the price (Option only)")
  public BigDecimal getIv() {
    return iv;
  }

  public void setIv(BigDecimal iv) {
    this.iv = iv;
  }

  public UserTrade tradeId(String tradeId) {
    this.tradeId = tradeId;
    return this;
  }

   /**
   * Unique (per currency) trade identifier
   * @return tradeId
  **/
  @ApiModelProperty(required = true, value = "Unique (per currency) trade identifier")
  public String getTradeId() {
    return tradeId;
  }

  public void setTradeId(String tradeId) {
    this.tradeId = tradeId;
  }

  public UserTrade fee(BigDecimal fee) {
    this.fee = fee;
    return this;
  }

   /**
   * User&#39;s fee in units of the specified &#x60;fee_currency&#x60;
   * @return fee
  **/
  @ApiModelProperty(required = true, value = "User's fee in units of the specified `fee_currency`")
  public BigDecimal getFee() {
    return fee;
  }

  public void setFee(BigDecimal fee) {
    this.fee = fee;
  }

  public UserTrade orderType(OrderTypeEnum orderType) {
    this.orderType = orderType;
    return this;
  }

   /**
   * Order type: &#x60;\&quot;limit&#x60;, &#x60;\&quot;market\&quot;&#x60;, or &#x60;\&quot;liquidation\&quot;&#x60;
   * @return orderType
  **/
  @ApiModelProperty(value = "Order type: `\"limit`, `\"market\"`, or `\"liquidation\"`")
  public OrderTypeEnum getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderTypeEnum orderType) {
    this.orderType = orderType;
  }

  public UserTrade tradeSeq(Integer tradeSeq) {
    this.tradeSeq = tradeSeq;
    return this;
  }

   /**
   * The sequence number of the trade within instrument
   * @return tradeSeq
  **/
  @ApiModelProperty(required = true, value = "The sequence number of the trade within instrument")
  public Integer getTradeSeq() {
    return tradeSeq;
  }

  public void setTradeSeq(Integer tradeSeq) {
    this.tradeSeq = tradeSeq;
  }

  public UserTrade selfTrade(Boolean selfTrade) {
    this.selfTrade = selfTrade;
    return this;
  }

   /**
   * &#x60;true&#x60; if the trade is against own order. This can only happen when your account has self-trading enabled. Contact an administrator if you think you need that
   * @return selfTrade
  **/
  @ApiModelProperty(required = true, value = "`true` if the trade is against own order. This can only happen when your account has self-trading enabled. Contact an administrator if you think you need that")
  public Boolean getSelfTrade() {
    return selfTrade;
  }

  public void setSelfTrade(Boolean selfTrade) {
    this.selfTrade = selfTrade;
  }

  public UserTrade state(StateEnum state) {
    this.state = state;
    return this;
  }

   /**
   * order state, &#x60;\&quot;open\&quot;&#x60;, &#x60;\&quot;filled\&quot;&#x60;, &#x60;\&quot;rejected\&quot;&#x60;, &#x60;\&quot;cancelled\&quot;&#x60;, &#x60;\&quot;untriggered\&quot;&#x60; or &#x60;\&quot;archive\&quot;&#x60; (if order was archived)
   * @return state
  **/
  @ApiModelProperty(required = true, value = "order state, `\"open\"`, `\"filled\"`, `\"rejected\"`, `\"cancelled\"`, `\"untriggered\"` or `\"archive\"` (if order was archived)")
  public StateEnum getState() {
    return state;
  }

  public void setState(StateEnum state) {
    this.state = state;
  }

  public UserTrade label(String label) {
    this.label = label;
    return this;
  }

   /**
   * User defined label (presented only when previously set for order by user)
   * @return label
  **/
  @ApiModelProperty(value = "User defined label (presented only when previously set for order by user)")
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public UserTrade indexPrice(BigDecimal indexPrice) {
    this.indexPrice = indexPrice;
    return this;
  }

   /**
   * Index Price at the moment of trade
   * @return indexPrice
  **/
  @ApiModelProperty(required = true, value = "Index Price at the moment of trade")
  public BigDecimal getIndexPrice() {
    return indexPrice;
  }

  public void setIndexPrice(BigDecimal indexPrice) {
    this.indexPrice = indexPrice;
  }

  public UserTrade amount(BigDecimal amount) {
    this.amount = amount;
    return this;
  }

   /**
   * Trade amount. For perpetual and futures - in USD units, for options it is amount of corresponding cryptocurrency contracts, e.g., BTC or ETH.
   * @return amount
  **/
  @ApiModelProperty(required = true, value = "Trade amount. For perpetual and futures - in USD units, for options it is amount of corresponding cryptocurrency contracts, e.g., BTC or ETH.")
  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public UserTrade instrumentName(String instrumentName) {
    this.instrumentName = instrumentName;
    return this;
  }

   /**
   * Unique instrument identifier
   * @return instrumentName
  **/
  @ApiModelProperty(example = "BTC-PERPETUAL", required = true, value = "Unique instrument identifier")
  public String getInstrumentName() {
    return instrumentName;
  }

  public void setInstrumentName(String instrumentName) {
    this.instrumentName = instrumentName;
  }

  public UserTrade tickDirection(TickDirectionEnum tickDirection) {
    this.tickDirection = tickDirection;
    return this;
  }

   /**
   * Direction of the \&quot;tick\&quot; (&#x60;0&#x60; &#x3D; Plus Tick, &#x60;1&#x60; &#x3D; Zero-Plus Tick, &#x60;2&#x60; &#x3D; Minus Tick, &#x60;3&#x60; &#x3D; Zero-Minus Tick).
   * @return tickDirection
  **/
  @ApiModelProperty(required = true, value = "Direction of the \"tick\" (`0` = Plus Tick, `1` = Zero-Plus Tick, `2` = Minus Tick, `3` = Zero-Minus Tick).")
  public TickDirectionEnum getTickDirection() {
    return tickDirection;
  }

  public void setTickDirection(TickDirectionEnum tickDirection) {
    this.tickDirection = tickDirection;
  }

  public UserTrade matchingId(String matchingId) {
    this.matchingId = matchingId;
    return this;
  }

   /**
   * Always &#x60;null&#x60;, except for a self-trade which is possible only if self-trading is switched on for the account (in that case this will be id of the maker order of the subscriber)
   * @return matchingId
  **/
  @ApiModelProperty(required = true, value = "Always `null`, except for a self-trade which is possible only if self-trading is switched on for the account (in that case this will be id of the maker order of the subscriber)")
  public String getMatchingId() {
    return matchingId;
  }

  public void setMatchingId(String matchingId) {
    this.matchingId = matchingId;
  }

  public UserTrade liquidity(LiquidityEnum liquidity) {
    this.liquidity = liquidity;
    return this;
  }

   /**
   * Describes what was role of users order: &#x60;\&quot;M\&quot;&#x60; when it was maker order, &#x60;\&quot;T\&quot;&#x60; when it was taker order
   * @return liquidity
  **/
  @ApiModelProperty(value = "Describes what was role of users order: `\"M\"` when it was maker order, `\"T\"` when it was taker order")
  public LiquidityEnum getLiquidity() {
    return liquidity;
  }

  public void setLiquidity(LiquidityEnum liquidity) {
    this.liquidity = liquidity;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserTrade userTrade = (UserTrade) o;
    return Objects.equals(this.direction, userTrade.direction) &&
        Objects.equals(this.feeCurrency, userTrade.feeCurrency) &&
        Objects.equals(this.orderId, userTrade.orderId) &&
        Objects.equals(this.timestamp, userTrade.timestamp) &&
        Objects.equals(this.price, userTrade.price) &&
        Objects.equals(this.iv, userTrade.iv) &&
        Objects.equals(this.tradeId, userTrade.tradeId) &&
        Objects.equals(this.fee, userTrade.fee) &&
        Objects.equals(this.orderType, userTrade.orderType) &&
        Objects.equals(this.tradeSeq, userTrade.tradeSeq) &&
        Objects.equals(this.selfTrade, userTrade.selfTrade) &&
        Objects.equals(this.state, userTrade.state) &&
        Objects.equals(this.label, userTrade.label) &&
        Objects.equals(this.indexPrice, userTrade.indexPrice) &&
        Objects.equals(this.amount, userTrade.amount) &&
        Objects.equals(this.instrumentName, userTrade.instrumentName) &&
        Objects.equals(this.tickDirection, userTrade.tickDirection) &&
        Objects.equals(this.matchingId, userTrade.matchingId) &&
        Objects.equals(this.liquidity, userTrade.liquidity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(direction, feeCurrency, orderId, timestamp, price, iv, tradeId, fee, orderType, tradeSeq, selfTrade, state, label, indexPrice, amount, instrumentName, tickDirection, matchingId, liquidity);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserTrade {\n");
    sb.append("    direction: ").append(toIndentedString(direction)).append("\n");
    sb.append("    feeCurrency: ").append(toIndentedString(feeCurrency)).append("\n");
    sb.append("    orderId: ").append(toIndentedString(orderId)).append("\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("    price: ").append(toIndentedString(price)).append("\n");
    sb.append("    iv: ").append(toIndentedString(iv)).append("\n");
    sb.append("    tradeId: ").append(toIndentedString(tradeId)).append("\n");
    sb.append("    fee: ").append(toIndentedString(fee)).append("\n");
    sb.append("    orderType: ").append(toIndentedString(orderType)).append("\n");
    sb.append("    tradeSeq: ").append(toIndentedString(tradeSeq)).append("\n");
    sb.append("    selfTrade: ").append(toIndentedString(selfTrade)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    indexPrice: ").append(toIndentedString(indexPrice)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    instrumentName: ").append(toIndentedString(instrumentName)).append("\n");
    sb.append("    tickDirection: ").append(toIndentedString(tickDirection)).append("\n");
    sb.append("    matchingId: ").append(toIndentedString(matchingId)).append("\n");
    sb.append("    liquidity: ").append(toIndentedString(liquidity)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

