/**
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
package org.openapitools.client.api

import org.openapitools.client.core._
import org.openapitools.client.core.CollectionFormats._
import org.openapitools.client.core.ApiKeyLocations._

object PrivateApi {

  def apply(baseUrl: String = "https://www.deribit.com/api/v2") = new PrivateApi(baseUrl)
}

class PrivateApi(baseUrl: String) {
  
  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param currency The currency symbol
   * @param `type` Address book type
   * @param address Address in currency format, it must be in address book
   * @param name Name of address book entry
   * @param tfa TFA code, required when TFA is enabled for current account
   */
  def privateAddToAddressBookGet(currency: String, `type`: String, address: String, name: String, tfa: Option[String] = None)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/add_to_address_book", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("currency", currency)
      .withQueryParam("type", `type`)
      .withQueryParam("address", address)
      .withQueryParam("name", name)
      .withQueryParam("tfa", tfa)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any (ok response)
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param instrumentName Instrument name
   * @param amount It represents the requested order size. For perpetual and futures the amount is in USD units, for options it is amount of corresponding cryptocurrency contracts, e.g., BTC or ETH
   * @param `type` The order type, default: `\"limit\"`
   * @param label user defined label for the order (maximum 32 characters)
   * @param price <p>The order price in base currency (Only for limit and stop_limit orders)</p> <p>When adding order with advanced=usd, the field price should be the option price value in USD.</p> <p>When adding order with advanced=implv, the field price should be a value of implied volatility in percentages. For example,  price=100, means implied volatility of 100%</p>
   * @param timeInForce <p>Specifies how long the order remains in effect. Default `\"good_til_cancelled\"`</p> <ul> <li>`\"good_til_cancelled\"` - unfilled order remains in order book until cancelled</li> <li>`\"fill_or_kill\"` - execute a transaction immediately and completely or not at all</li> <li>`\"immediate_or_cancel\"` - execute a transaction immediately, and any portion of the order that cannot be immediately filled is cancelled</li> </ul>
   * @param maxShow Maximum amount within an order to be shown to other customers, `0` for invisible order
   * @param postOnly <p>If true, the order is considered post-only. If the new price would cause the order to be filled immediately (as taker), the price will be changed to be just below the bid.</p> <p>Only valid in combination with time_in_force=`\"good_til_cancelled\"`</p>
   * @param reduceOnly If `true`, the order is considered reduce-only which is intended to only reduce a current position
   * @param stopPrice Stop price, required for stop limit orders (Only for stop orders)
   * @param trigger Defines trigger type, required for `\"stop_limit\"` order type
   * @param advanced Advanced option order type. (Only for options)
   */
  def privateBuyGet(instrumentName: String, amount: Double, `type`: Option[String] = None, label: Option[String] = None, price: Option[Double] = None, timeInForce: Option[String] = None, maxShow: Option[Double] = None, postOnly: Option[Boolean] = None, reduceOnly: Option[Boolean] = None, stopPrice: Option[Double] = None, trigger: Option[String] = None, advanced: Option[String] = None)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/buy", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("instrument_name", instrumentName)
      .withQueryParam("amount", amount)
      .withQueryParam("type", `type`)
      .withQueryParam("label", label)
      .withQueryParam("price", price)
      .withQueryParam("time_in_force", timeInForce)
      .withQueryParam("max_show", maxShow)
      .withQueryParam("post_only", postOnly)
      .withQueryParam("reduce_only", reduceOnly)
      .withQueryParam("stop_price", stopPrice)
      .withQueryParam("trigger", trigger)
      .withQueryParam("advanced", advanced)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param currency The currency symbol
   * @param kind Instrument kind, if not provided instruments of all kinds are considered
   * @param `type` Order type - limit, stop or all, default - `all`
   */
  def privateCancelAllByCurrencyGet(currency: String, kind: Option[String] = None, `type`: Option[String] = None)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/cancel_all_by_currency", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("currency", currency)
      .withQueryParam("kind", kind)
      .withQueryParam("type", `type`)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param instrumentName Instrument name
   * @param `type` Order type - limit, stop or all, default - `all`
   */
  def privateCancelAllByInstrumentGet(instrumentName: String, `type`: Option[String] = None)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/cancel_all_by_instrument", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("instrument_name", instrumentName)
      .withQueryParam("type", `type`)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   */
  def privateCancelAllGet()(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/cancel_all", "application/json")
      .withCredentials(basicAuth)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param orderId The order id
   */
  def privateCancelGet(orderId: String)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/cancel", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("order_id", orderId)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any (ok response)
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param currency The currency symbol
   * @param id Id of transfer
   * @param tfa TFA code, required when TFA is enabled for current account
   */
  def privateCancelTransferByIdGet(currency: String, id: Int, tfa: Option[String] = None)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/cancel_transfer_by_id", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("currency", currency)
      .withQueryParam("id", id)
      .withQueryParam("tfa", tfa)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param currency The currency symbol
   * @param id The withdrawal id
   */
  def privateCancelWithdrawalGet(currency: String, id: Double)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/cancel_withdrawal", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("currency", currency)
      .withQueryParam("id", id)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any (ok response)
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param sid The user id for the subaccount
   * @param name The new user name
   */
  def privateChangeSubaccountNameGet(sid: Int, name: String)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/change_subaccount_name", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("sid", sid)
      .withQueryParam("name", name)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any (ok response)
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param instrumentName Instrument name
   * @param `type` The order type
   * @param price Optional price for limit order.
   */
  def privateClosePositionGet(instrumentName: String, `type`: String, price: Option[Double] = None)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/close_position", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("instrument_name", instrumentName)
      .withQueryParam("type", `type`)
      .withQueryParam("price", price)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param currency The currency symbol
   */
  def privateCreateDepositAddressGet(currency: String)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/create_deposit_address", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("currency", currency)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any (ok response)
   * 
   * Available security schemes:
   *   bearerAuth (http)
   */
  def privateCreateSubaccountGet()(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/create_subaccount", "application/json")
      .withCredentials(basicAuth)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any (ok response)
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param sid The user id for the subaccount
   */
  def privateDisableTfaForSubaccountGet(sid: Int)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/disable_tfa_for_subaccount", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("sid", sid)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any (ok response)
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param password The password for the subaccount
   * @param code One time recovery code
   */
  def privateDisableTfaWithRecoveryCodeGet(password: String, code: String)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/disable_tfa_with_recovery_code", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("password", password)
      .withQueryParam("code", code)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param orderId The order id
   * @param amount It represents the requested order size. For perpetual and futures the amount is in USD units, for options it is amount of corresponding cryptocurrency contracts, e.g., BTC or ETH
   * @param price <p>The order price in base currency.</p> <p>When editing an option order with advanced=usd, the field price should be the option price value in USD.</p> <p>When editing an option order with advanced=implv, the field price should be a value of implied volatility in percentages. For example,  price=100, means implied volatility of 100%</p>
   * @param postOnly <p>If true, the order is considered post-only. If the new price would cause the order to be filled immediately (as taker), the price will be changed to be just below the bid.</p> <p>Only valid in combination with time_in_force=`\"good_til_cancelled\"`</p>
   * @param advanced Advanced option order type. If you have posted an advanced option order, it is necessary to re-supply this parameter when editing it (Only for options)
   * @param stopPrice Stop price, required for stop limit orders (Only for stop orders)
   */
  def privateEditGet(orderId: String, amount: Double, price: Double, postOnly: Option[Boolean] = None, advanced: Option[String] = None, stopPrice: Option[Double] = None)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/edit", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("order_id", orderId)
      .withQueryParam("amount", amount)
      .withQueryParam("price", price)
      .withQueryParam("post_only", postOnly)
      .withQueryParam("advanced", advanced)
      .withQueryParam("stop_price", stopPrice)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param currency The currency symbol
   * @param extended Include additional fields
   */
  def privateGetAccountSummaryGet(currency: String, extended: Option[Boolean] = None)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/get_account_summary", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("currency", currency)
      .withQueryParam("extended", extended)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param currency The currency symbol
   * @param `type` Address book type
   */
  def privateGetAddressBookGet(currency: String, `type`: String)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/get_address_book", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("currency", currency)
      .withQueryParam("type", `type`)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param currency The currency symbol
   */
  def privateGetCurrentDepositAddressGet(currency: String)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/get_current_deposit_address", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("currency", currency)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param currency The currency symbol
   * @param count Number of requested items, default - `10`
   * @param offset The offset for pagination, default - `0`
   */
  def privateGetDepositsGet(currency: String, count: Option[Int] = None, offset: Option[Int] = None)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/get_deposits", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("currency", currency)
      .withQueryParam("count", count)
      .withQueryParam("offset", offset)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   */
  def privateGetEmailLanguageGet()(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/get_email_language", "application/json")
      .withCredentials(basicAuth)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param instrumentName Instrument name
   * @param amount Amount, integer for future, float for option. For perpetual and futures the amount is in USD units, for options it is amount of corresponding cryptocurrency contracts, e.g., BTC or ETH.
   * @param price Price
   */
  def privateGetMarginsGet(instrumentName: String, amount: Double, price: Double)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/get_margins", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("instrument_name", instrumentName)
      .withQueryParam("amount", amount)
      .withQueryParam("price", price)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   */
  def privateGetNewAnnouncementsGet()(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/get_new_announcements", "application/json")
      .withCredentials(basicAuth)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param currency The currency symbol
   * @param kind Instrument kind, if not provided instruments of all kinds are considered
   * @param `type` Order type, default - `all`
   */
  def privateGetOpenOrdersByCurrencyGet(currency: String, kind: Option[String] = None, `type`: Option[String] = None)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/get_open_orders_by_currency", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("currency", currency)
      .withQueryParam("kind", kind)
      .withQueryParam("type", `type`)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param instrumentName Instrument name
   * @param `type` Order type, default - `all`
   */
  def privateGetOpenOrdersByInstrumentGet(instrumentName: String, `type`: Option[String] = None)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/get_open_orders_by_instrument", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("instrument_name", instrumentName)
      .withQueryParam("type", `type`)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param currency The currency symbol
   * @param kind Instrument kind, if not provided instruments of all kinds are considered
   * @param count Number of requested items, default - `20`
   * @param offset The offset for pagination, default - `0`
   * @param includeOld Include in result orders older than 2 days, default - `false`
   * @param includeUnfilled Include in result fully unfilled closed orders, default - `false`
   */
  def privateGetOrderHistoryByCurrencyGet(currency: String, kind: Option[String] = None, count: Option[Int] = None, offset: Option[Int] = None, includeOld: Option[Boolean] = None, includeUnfilled: Option[Boolean] = None)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/get_order_history_by_currency", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("currency", currency)
      .withQueryParam("kind", kind)
      .withQueryParam("count", count)
      .withQueryParam("offset", offset)
      .withQueryParam("include_old", includeOld)
      .withQueryParam("include_unfilled", includeUnfilled)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param instrumentName Instrument name
   * @param count Number of requested items, default - `20`
   * @param offset The offset for pagination, default - `0`
   * @param includeOld Include in result orders older than 2 days, default - `false`
   * @param includeUnfilled Include in result fully unfilled closed orders, default - `false`
   */
  def privateGetOrderHistoryByInstrumentGet(instrumentName: String, count: Option[Int] = None, offset: Option[Int] = None, includeOld: Option[Boolean] = None, includeUnfilled: Option[Boolean] = None)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/get_order_history_by_instrument", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("instrument_name", instrumentName)
      .withQueryParam("count", count)
      .withQueryParam("offset", offset)
      .withQueryParam("include_old", includeOld)
      .withQueryParam("include_unfilled", includeUnfilled)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param ids Ids of orders
   */
  def privateGetOrderMarginByIdsGet(ids: Seq[String])(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/get_order_margin_by_ids", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("ids", ArrayValues(ids, MULTI))
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any (ok response)
   *   code 400 : Any (result when used via rest/HTTP)
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param orderId The order id
   */
  def privateGetOrderStateGet(orderId: String)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/get_order_state", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("order_id", orderId)
      .withSuccessResponse[Any](200)
      .withErrorResponse[Any](400)
      

  /**
   * Expected answers:
   *   code 200 : Any (When successful returns position)
   *   code 400 : Any (When some error occurs)
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param instrumentName Instrument name
   */
  def privateGetPositionGet(instrumentName: String)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/get_position", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("instrument_name", instrumentName)
      .withSuccessResponse[Any](200)
      .withErrorResponse[Any](400)
      

  /**
   * Expected answers:
   *   code 200 : Any (When successful returns array of positions)
   *   code 400 : Any (When some error occurs)
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param currency 
   * @param kind Kind filter on positions
   */
  def privateGetPositionsGet(currency: String, kind: Option[String] = None)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/get_positions", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("currency", currency)
      .withQueryParam("kind", kind)
      .withSuccessResponse[Any](200)
      .withErrorResponse[Any](400)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param currency The currency symbol
   * @param `type` Settlement type
   * @param count Number of requested items, default - `20`
   */
  def privateGetSettlementHistoryByCurrencyGet(currency: String, `type`: Option[String] = None, count: Option[Int] = None)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/get_settlement_history_by_currency", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("currency", currency)
      .withQueryParam("type", `type`)
      .withQueryParam("count", count)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param instrumentName Instrument name
   * @param `type` Settlement type
   * @param count Number of requested items, default - `20`
   */
  def privateGetSettlementHistoryByInstrumentGet(instrumentName: String, `type`: Option[String] = None, count: Option[Int] = None)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/get_settlement_history_by_instrument", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("instrument_name", instrumentName)
      .withQueryParam("type", `type`)
      .withQueryParam("count", count)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any (ok response)
   *   code 401 : Any (not authorised)
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param withPortfolio 
   */
  def privateGetSubaccountsGet(withPortfolio: Option[Boolean] = None)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/get_subaccounts", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("with_portfolio", withPortfolio)
      .withSuccessResponse[Any](200)
      .withErrorResponse[Any](401)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param currency The currency symbol
   * @param count Number of requested items, default - `10`
   * @param offset The offset for pagination, default - `0`
   */
  def privateGetTransfersGet(currency: String, count: Option[Int] = None, offset: Option[Int] = None)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/get_transfers", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("currency", currency)
      .withQueryParam("count", count)
      .withQueryParam("offset", offset)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param currency The currency symbol
   * @param startTimestamp The earliest timestamp to return result for
   * @param endTimestamp The most recent timestamp to return result for
   * @param kind Instrument kind, if not provided instruments of all kinds are considered
   * @param count Number of requested items, default - `10`
   * @param includeOld Include trades older than 7 days, default - `false`
   * @param sorting Direction of results sorting (`default` value means no sorting, results will be returned in order in which they left the database)
   */
  def privateGetUserTradesByCurrencyAndTimeGet(currency: String, startTimestamp: Int, endTimestamp: Int, kind: Option[String] = None, count: Option[Int] = None, includeOld: Option[Boolean] = None, sorting: Option[String] = None)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/get_user_trades_by_currency_and_time", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("currency", currency)
      .withQueryParam("kind", kind)
      .withQueryParam("start_timestamp", startTimestamp)
      .withQueryParam("end_timestamp", endTimestamp)
      .withQueryParam("count", count)
      .withQueryParam("include_old", includeOld)
      .withQueryParam("sorting", sorting)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param currency The currency symbol
   * @param kind Instrument kind, if not provided instruments of all kinds are considered
   * @param startId The ID number of the first trade to be returned
   * @param endId The ID number of the last trade to be returned
   * @param count Number of requested items, default - `10`
   * @param includeOld Include trades older than 7 days, default - `false`
   * @param sorting Direction of results sorting (`default` value means no sorting, results will be returned in order in which they left the database)
   */
  def privateGetUserTradesByCurrencyGet(currency: String, kind: Option[String] = None, startId: Option[String] = None, endId: Option[String] = None, count: Option[Int] = None, includeOld: Option[Boolean] = None, sorting: Option[String] = None)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/get_user_trades_by_currency", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("currency", currency)
      .withQueryParam("kind", kind)
      .withQueryParam("start_id", startId)
      .withQueryParam("end_id", endId)
      .withQueryParam("count", count)
      .withQueryParam("include_old", includeOld)
      .withQueryParam("sorting", sorting)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param instrumentName Instrument name
   * @param startTimestamp The earliest timestamp to return result for
   * @param endTimestamp The most recent timestamp to return result for
   * @param count Number of requested items, default - `10`
   * @param includeOld Include trades older than 7 days, default - `false`
   * @param sorting Direction of results sorting (`default` value means no sorting, results will be returned in order in which they left the database)
   */
  def privateGetUserTradesByInstrumentAndTimeGet(instrumentName: String, startTimestamp: Int, endTimestamp: Int, count: Option[Int] = None, includeOld: Option[Boolean] = None, sorting: Option[String] = None)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/get_user_trades_by_instrument_and_time", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("instrument_name", instrumentName)
      .withQueryParam("start_timestamp", startTimestamp)
      .withQueryParam("end_timestamp", endTimestamp)
      .withQueryParam("count", count)
      .withQueryParam("include_old", includeOld)
      .withQueryParam("sorting", sorting)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param instrumentName Instrument name
   * @param startSeq The sequence number of the first trade to be returned
   * @param endSeq The sequence number of the last trade to be returned
   * @param count Number of requested items, default - `10`
   * @param includeOld Include trades older than 7 days, default - `false`
   * @param sorting Direction of results sorting (`default` value means no sorting, results will be returned in order in which they left the database)
   */
  def privateGetUserTradesByInstrumentGet(instrumentName: String, startSeq: Option[Int] = None, endSeq: Option[Int] = None, count: Option[Int] = None, includeOld: Option[Boolean] = None, sorting: Option[String] = None)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/get_user_trades_by_instrument", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("instrument_name", instrumentName)
      .withQueryParam("start_seq", startSeq)
      .withQueryParam("end_seq", endSeq)
      .withQueryParam("count", count)
      .withQueryParam("include_old", includeOld)
      .withQueryParam("sorting", sorting)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param orderId The order id
   * @param sorting Direction of results sorting (`default` value means no sorting, results will be returned in order in which they left the database)
   */
  def privateGetUserTradesByOrderGet(orderId: String, sorting: Option[String] = None)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/get_user_trades_by_order", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("order_id", orderId)
      .withQueryParam("sorting", sorting)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param currency The currency symbol
   * @param count Number of requested items, default - `10`
   * @param offset The offset for pagination, default - `0`
   */
  def privateGetWithdrawalsGet(currency: String, count: Option[Int] = None, offset: Option[Int] = None)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/get_withdrawals", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("currency", currency)
      .withQueryParam("count", count)
      .withQueryParam("offset", offset)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param currency The currency symbol
   * @param `type` Address book type
   * @param address Address in currency format, it must be in address book
   * @param tfa TFA code, required when TFA is enabled for current account
   */
  def privateRemoveFromAddressBookGet(currency: String, `type`: String, address: String, tfa: Option[String] = None)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/remove_from_address_book", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("currency", currency)
      .withQueryParam("type", `type`)
      .withQueryParam("address", address)
      .withQueryParam("tfa", tfa)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any (ok response)
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param instrumentName Instrument name
   * @param amount It represents the requested order size. For perpetual and futures the amount is in USD units, for options it is amount of corresponding cryptocurrency contracts, e.g., BTC or ETH
   * @param `type` The order type, default: `\"limit\"`
   * @param label user defined label for the order (maximum 32 characters)
   * @param price <p>The order price in base currency (Only for limit and stop_limit orders)</p> <p>When adding order with advanced=usd, the field price should be the option price value in USD.</p> <p>When adding order with advanced=implv, the field price should be a value of implied volatility in percentages. For example,  price=100, means implied volatility of 100%</p>
   * @param timeInForce <p>Specifies how long the order remains in effect. Default `\"good_til_cancelled\"`</p> <ul> <li>`\"good_til_cancelled\"` - unfilled order remains in order book until cancelled</li> <li>`\"fill_or_kill\"` - execute a transaction immediately and completely or not at all</li> <li>`\"immediate_or_cancel\"` - execute a transaction immediately, and any portion of the order that cannot be immediately filled is cancelled</li> </ul>
   * @param maxShow Maximum amount within an order to be shown to other customers, `0` for invisible order
   * @param postOnly <p>If true, the order is considered post-only. If the new price would cause the order to be filled immediately (as taker), the price will be changed to be just below the bid.</p> <p>Only valid in combination with time_in_force=`\"good_til_cancelled\"`</p>
   * @param reduceOnly If `true`, the order is considered reduce-only which is intended to only reduce a current position
   * @param stopPrice Stop price, required for stop limit orders (Only for stop orders)
   * @param trigger Defines trigger type, required for `\"stop_limit\"` order type
   * @param advanced Advanced option order type. (Only for options)
   */
  def privateSellGet(instrumentName: String, amount: Double, `type`: Option[String] = None, label: Option[String] = None, price: Option[Double] = None, timeInForce: Option[String] = None, maxShow: Option[Double] = None, postOnly: Option[Boolean] = None, reduceOnly: Option[Boolean] = None, stopPrice: Option[Double] = None, trigger: Option[String] = None, advanced: Option[String] = None)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/sell", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("instrument_name", instrumentName)
      .withQueryParam("amount", amount)
      .withQueryParam("type", `type`)
      .withQueryParam("label", label)
      .withQueryParam("price", price)
      .withQueryParam("time_in_force", timeInForce)
      .withQueryParam("max_show", maxShow)
      .withQueryParam("post_only", postOnly)
      .withQueryParam("reduce_only", reduceOnly)
      .withQueryParam("stop_price", stopPrice)
      .withQueryParam("trigger", trigger)
      .withQueryParam("advanced", advanced)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param announcementId the ID of the announcement
   */
  def privateSetAnnouncementAsReadGet(announcementId: Double)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/set_announcement_as_read", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("announcement_id", announcementId)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any (ok response)
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param sid The user id for the subaccount
   * @param email The email address for the subaccount
   */
  def privateSetEmailForSubaccountGet(sid: Int, email: String)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/set_email_for_subaccount", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("sid", sid)
      .withQueryParam("email", email)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param language The abbreviated language name. Valid values include `\"en\"`, `\"ko\"`, `\"zh\"`
   */
  def privateSetEmailLanguageGet(language: String)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/set_email_language", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("language", language)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any (ok response)
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param sid The user id for the subaccount
   * @param password The password for the subaccount
   */
  def privateSetPasswordForSubaccountGet(sid: Int, password: String)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/set_password_for_subaccount", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("sid", sid)
      .withQueryParam("password", password)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any (ok response)
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param currency The currency symbol
   * @param amount Amount of funds to be transferred
   * @param destination Id of destination subaccount
   */
  def privateSubmitTransferToSubaccountGet(currency: String, amount: Double, destination: Int)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/submit_transfer_to_subaccount", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("currency", currency)
      .withQueryParam("amount", amount)
      .withQueryParam("destination", destination)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param currency The currency symbol
   * @param amount Amount of funds to be transferred
   * @param destination Destination address from address book
   * @param tfa TFA code, required when TFA is enabled for current account
   */
  def privateSubmitTransferToUserGet(currency: String, amount: Double, destination: String, tfa: Option[String] = None)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/submit_transfer_to_user", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("currency", currency)
      .withQueryParam("amount", amount)
      .withQueryParam("destination", destination)
      .withQueryParam("tfa", tfa)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param currency The currency symbol
   * @param state 
   */
  def privateToggleDepositAddressCreationGet(currency: String, state: Boolean)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/toggle_deposit_address_creation", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("currency", currency)
      .withQueryParam("state", state)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any (ok response)
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param sid The user id for the subaccount
   * @param state enable (`true`) or disable (`false`) notifications
   */
  def privateToggleNotificationsFromSubaccountGet(sid: Int, state: Boolean)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/toggle_notifications_from_subaccount", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("sid", sid)
      .withQueryParam("state", state)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any (ok response)
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param sid The user id for the subaccount
   * @param state enable or disable login.
   */
  def privateToggleSubaccountLoginGet(sid: Int, state: String)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/toggle_subaccount_login", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("sid", sid)
      .withQueryParam("state", state)
      .withSuccessResponse[Any](200)
      

  /**
   * Expected answers:
   *   code 200 : Any 
   * 
   * Available security schemes:
   *   bearerAuth (http)
   * 
   * @param currency The currency symbol
   * @param address Address in currency format, it must be in address book
   * @param amount Amount of funds to be withdrawn
   * @param priority Withdrawal priority, optional for BTC, default: `high`
   * @param tfa TFA code, required when TFA is enabled for current account
   */
  def privateWithdrawGet(currency: String, address: String, amount: Double, priority: Option[String] = None, tfa: Option[String] = None)(implicit basicAuth: BasicCredentials): ApiRequest[Any] =
    ApiRequest[Any](ApiMethods.GET, "https://www.deribit.com/api/v2", "/private/withdraw", "application/json")
      .withCredentials(basicAuth)
      .withQueryParam("currency", currency)
      .withQueryParam("address", address)
      .withQueryParam("amount", amount)
      .withQueryParam("priority", priority)
      .withQueryParam("tfa", tfa)
      .withSuccessResponse[Any](200)
      



}

