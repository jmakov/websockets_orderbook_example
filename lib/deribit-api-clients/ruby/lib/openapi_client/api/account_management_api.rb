=begin
#Deribit API

##Overview  Deribit provides three different interfaces to access the API:  * [JSON-RPC over Websocket](#json-rpc) * [JSON-RPC over HTTP](#json-rpc) * [FIX](#fix-api) (Financial Information eXchange)  With the API Console you can use and test the JSON-RPC API, both via HTTP and  via Websocket. To visit the API console, go to __Account > API tab >  API Console tab.__   ##Naming Deribit tradeable assets or instruments use the following system of naming:  |Kind|Examples|Template|Comments| |----|--------|--------|--------| |Future|<code>BTC-25MAR16</code>, <code>BTC-5AUG16</code>|<code>BTC-DMMMYY</code>|<code>BTC</code> is currency, <code>DMMMYY</code> is expiration date, <code>D</code> stands for day of month (1 or 2 digits), <code>MMM</code> - month (3 first letters in English), <code>YY</code> stands for year.| |Perpetual|<code>BTC-PERPETUAL</code>                        ||Perpetual contract for currency <code>BTC</code>.| |Option|<code>BTC-25MAR16-420-C</code>, <code>BTC-5AUG16-580-P</code>|<code>BTC-DMMMYY-STRIKE-K</code>|<code>STRIKE</code> is option strike price in USD. Template <code>K</code> is option kind: <code>C</code> for call options or <code>P</code> for put options.|   # JSON-RPC JSON-RPC is a light-weight remote procedure call (RPC) protocol. The  [JSON-RPC specification](https://www.jsonrpc.org/specification) defines the data structures that are used for the messages that are exchanged between client and server, as well as the rules around their processing. JSON-RPC uses JSON (RFC 4627) as data format.  JSON-RPC is transport agnostic: it does not specify which transport mechanism must be used. The Deribit API supports both Websocket (preferred) and HTTP (with limitations: subscriptions are not supported over HTTP).  ## Request messages > An example of a request message:  ```json {     \"jsonrpc\": \"2.0\",     \"id\": 8066,     \"method\": \"public/ticker\",     \"params\": {         \"instrument\": \"BTC-24AUG18-6500-P\"     } } ```  According to the JSON-RPC sepcification the requests must be JSON objects with the following fields.  |Name|Type|Description| |----|----|-----------| |jsonrpc|string|The version of the JSON-RPC spec: \"2.0\"| |id|integer or string|An identifier of the request. If it is included, then the response will contain the same identifier| |method|string|The method to be invoked| |params|object|The parameters values for the method. The field names must match with the expected parameter names. The parameters that are expected are described in the documentation for the methods, below.|  <aside class=\"warning\"> The JSON-RPC specification describes two features that are currently not supported by the API:  <ul> <li>Specification of parameter values by position</li> <li>Batch requests</li> </ul>  </aside>   ## Response messages > An example of a response message:  ```json {     \"jsonrpc\": \"2.0\",     \"id\": 5239,     \"testnet\": false,     \"result\": [         {             \"currency\": \"BTC\",             \"currencyLong\": \"Bitcoin\",             \"minConfirmation\": 2,             \"txFee\": 0.0006,             \"isActive\": true,             \"coinType\": \"BITCOIN\",             \"baseAddress\": null         }     ],     \"usIn\": 1535043730126248,     \"usOut\": 1535043730126250,     \"usDiff\": 2 } ```  The JSON-RPC API always responds with a JSON object with the following fields.   |Name|Type|Description| |----|----|-----------| |id|integer|This is the same id that was sent in the request.| |result|any|If successful, the result of the API call. The format for the result is described with each method.| |error|error object|Only present if there was an error invoking the method. The error object is described below.| |testnet|boolean|Indicates whether the API in use is actually the test API.  <code>false</code> for production server, <code>true</code> for test server.| |usIn|integer|The timestamp when the requests was received (microseconds since the Unix epoch)| |usOut|integer|The timestamp when the response was sent (microseconds since the Unix epoch)| |usDiff|integer|The number of microseconds that was spent handling the request|  <aside class=\"notice\"> The fields <code>testnet</code>, <code>usIn</code>, <code>usOut</code> and <code>usDiff</code> are not part of the JSON-RPC standard.  <p>In order not to clutter the examples they will generally be omitted from the example code.</p> </aside>  > An example of a response with an error:  ```json {     \"jsonrpc\": \"2.0\",     \"id\": 8163,     \"error\": {         \"code\": 11050,         \"message\": \"bad_request\"     },     \"testnet\": false,     \"usIn\": 1535037392434763,     \"usOut\": 1535037392448119,     \"usDiff\": 13356 } ``` In case of an error the response message will contain the error field, with as value an object with the following with the following fields:  |Name|Type|Description |----|----|-----------| |code|integer|A number that indicates the kind of error.| |message|string|A short description that indicates the kind of error.| |data|any|Additional data about the error. This field may be omitted.|  ## Notifications  > An example of a notification:  ```json {     \"jsonrpc\": \"2.0\",     \"method\": \"subscription\",     \"params\": {         \"channel\": \"deribit_price_index.btc_usd\",         \"data\": {             \"timestamp\": 1535098298227,             \"price\": 6521.17,             \"index_name\": \"btc_usd\"         }     } } ```  API users can subscribe to certain types of notifications. This means that they will receive JSON-RPC notification-messages from the server when certain events occur, such as changes to the index price or changes to the order book for a certain instrument.   The API methods [public/subscribe](#public-subscribe) and [private/subscribe](#private-subscribe) are used to set up a subscription. Since HTTP does not support the sending of messages from server to client, these methods are only availble when using the Websocket transport mechanism.  At the moment of subscription a \"channel\" must be specified. The channel determines the type of events that will be received.  See [Subscriptions](#subscriptions) for more details about the channels.  In accordance with the JSON-RPC specification, the format of a notification  is that of a request message without an <code>id</code> field. The value of the <code>method</code> field will always be <code>\"subscription\"</code>. The <code>params</code> field will always be an object with 2 members: <code>channel</code> and <code>data</code>. The value of the <code>channel</code> member is the name of the channel (a string). The value of the <code>data</code> member is an object that contains data  that is specific for the channel.   ## Authentication  > An example of a JSON request with token:  ```json {     \"id\": 5647,     \"method\": \"private/get_subaccounts\",     \"params\": {         \"access_token\": \"67SVutDoVZSzkUStHSuk51WntMNBJ5mh5DYZhwzpiqDF\"     } } ```  The API consists of `public` and `private` methods. The public methods do not require authentication. The private methods use OAuth 2.0 authentication. This means that a valid OAuth access token must be included in the request, which can get achived by calling method [public/auth](#public-auth).  When the token was assigned to the user, it should be passed along, with other request parameters, back to the server:  |Connection type|Access token placement |----|-----------| |**Websocket**|Inside request JSON parameters, as an `access_token` field| |**HTTP (REST)**|Header `Authorization: bearer ```Token``` ` value|  ### Additional authorization method - basic user credentials  <span style=\"color:red\"><b> ! Not recommended - however, it could be useful for quick testing API</b></span></br>  Every `private` method could be accessed by providing, inside HTTP `Authorization: Basic XXX` header, values with user `ClientId` and assigned `ClientSecret` (both values can be found on the API page on the Deribit website) encoded with `Base64`:  <code>Authorization: Basic BASE64(`ClientId` + `:` + `ClientSecret`)</code>   ### Additional authorization method - Deribit signature credentials  The Derbit service provides dedicated authorization method, which harness user generated signature to increase security level for passing request data. Generated value is passed inside `Authorization` header, coded as:  <code>Authorization: deri-hmac-sha256 id=```ClientId```,ts=```Timestamp```,sig=```Signature```,nonce=```Nonce```</code>  where:  |Deribit credential|Description |----|-----------| |*ClientId*|Can be found on the API page on the Deribit website| |*Timestamp*|Time when the request was generated - given as **miliseconds**. It's valid for **60 seconds** since generation, after that time any request with an old timestamp will be rejected.| |*Signature*|Value for signature calculated as described below | |*Nonce*|Single usage, user generated initialization vector for the server token|  The signature is generated by the following formula:  <code> Signature = HEX_STRING( HMAC-SHA256( ClientSecret, StringToSign ) );</code></br>  <code> StringToSign =  Timestamp + \"\\n\" + Nonce + \"\\n\" + RequestData;</code></br>  <code> RequestData =  UPPERCASE(HTTP_METHOD())  + \"\\n\" + URI() + \"\\n\" + RequestBody + \"\\n\";</code></br>   e.g. (using shell with ```openssl``` tool):  <code>&nbsp;&nbsp;&nbsp;&nbsp;ClientId=AAAAAAAAAAA</code></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;ClientSecret=ABCD</code></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;Timestamp=$( date +%s000 )</code></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;Nonce=$( cat /dev/urandom | tr -dc 'a-z0-9' | head -c8 )</code></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;URI=\"/api/v2/private/get_account_summary?currency=BTC\"</code></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;HttpMethod=GET</code></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;Body=\"\"</code></br></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;Signature=$( echo -ne \"${Timestamp}\\n${Nonce}\\n${HttpMethod}\\n${URI}\\n${Body}\\n\" | openssl sha256 -r -hmac \"$ClientSecret\" | cut -f1 -d' ' )</code></br></br> <code>&nbsp;&nbsp;&nbsp;&nbsp;echo $Signature</code></br></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;shell output> ea40d5e5e4fae235ab22b61da98121fbf4acdc06db03d632e23c66bcccb90d2c  (**WARNING**: Exact value depends on current timestamp and client credentials</code></br></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;curl -s -X ${HttpMethod} -H \"Authorization: deri-hmac-sha256 id=${ClientId},ts=${Timestamp},nonce=${Nonce},sig=${Signature}\" \"https://www.deribit.com${URI}\"</code></br></br>    ### Additional authorization method - signature credentials (WebSocket API)  When connecting through Websocket, user can request for authorization using ```client_credential``` method, which requires providing following parameters (as a part of JSON request):  |JSON parameter|Description |----|-----------| |*grant_type*|Must be **client_signature**| |*client_id*|Can be found on the API page on the Deribit website| |*timestamp*|Time when the request was generated - given as **miliseconds**. It's valid for **60 seconds** since generation, after that time any request with an old timestamp will be rejected.| |*signature*|Value for signature calculated as described below | |*nonce*|Single usage, user generated initialization vector for the server token| |*data*|**Optional** field, which contains any user specific value|  The signature is generated by the following formula:  <code> StringToSign =  Timestamp + \"\\n\" + Nonce + \"\\n\" + Data;</code></br>  <code> Signature = HEX_STRING( HMAC-SHA256( ClientSecret, StringToSign ) );</code></br>   e.g. (using shell with ```openssl``` tool):  <code>&nbsp;&nbsp;&nbsp;&nbsp;ClientId=AAAAAAAAAAA</code></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;ClientSecret=ABCD</code></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;Timestamp=$( date +%s000 ) # e.g. 1554883365000 </code></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;Nonce=$( cat /dev/urandom | tr -dc 'a-z0-9' | head -c8 ) # e.g. fdbmmz79 </code></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;Data=\"\"</code></br></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;Signature=$( echo -ne \"${Timestamp}\\n${Nonce}\\n${Data}\\n\" | openssl sha256 -r -hmac \"$ClientSecret\" | cut -f1 -d' ' )</code></br></br> <code>&nbsp;&nbsp;&nbsp;&nbsp;echo $Signature</code></br></br>  <code>&nbsp;&nbsp;&nbsp;&nbsp;shell output> e20c9cd5639d41f8bbc88f4d699c4baf94a4f0ee320e9a116b72743c449eb994  (**WARNING**: Exact value depends on current timestamp and client credentials</code></br></br>   You can also check the signature value using some online tools like, e.g: [https://codebeautify.org/hmac-generator](https://codebeautify.org/hmac-generator) (but don't forget about adding *newline* after each part of the hashed text and remember that you **should use** it only with your **test credentials**).   Here's a sample JSON request created using the values from the example above:  <code> {                            </br> &nbsp;&nbsp;\"jsonrpc\" : \"2.0\",         </br> &nbsp;&nbsp;\"id\" : 9929,               </br> &nbsp;&nbsp;\"method\" : \"public/auth\",  </br> &nbsp;&nbsp;\"params\" :                 </br> &nbsp;&nbsp;{                        </br> &nbsp;&nbsp;&nbsp;&nbsp;\"grant_type\" : \"client_signature\",   </br> &nbsp;&nbsp;&nbsp;&nbsp;\"client_id\" : \"AAAAAAAAAAA\",         </br> &nbsp;&nbsp;&nbsp;&nbsp;\"timestamp\": \"1554883365000\",        </br> &nbsp;&nbsp;&nbsp;&nbsp;\"nonce\": \"fdbmmz79\",                 </br> &nbsp;&nbsp;&nbsp;&nbsp;\"data\": \"\",                          </br> &nbsp;&nbsp;&nbsp;&nbsp;\"signature\" : \"e20c9cd5639d41f8bbc88f4d699c4baf94a4f0ee320e9a116b72743c449eb994\"  </br> &nbsp;&nbsp;}                        </br> }                            </br> </code>   ### Access scope  When asking for `access token` user can provide the required access level (called `scope`) which defines what type of functionality he/she wants to use, and whether requests are only going to check for some data or also to update them.  Scopes are required and checked for `private` methods, so if you plan to use only `public` information you can stay with values assigned by default.  |Scope|Description |----|-----------| |*account:read*|Access to **account** methods - read only data| |*account:read_write*|Access to **account** methods - allows to manage account settings, add subaccounts, etc.| |*trade:read*|Access to **trade** methods - read only data| |*trade:read_write*|Access to **trade** methods - required to create and modify orders| |*wallet:read*|Access to **wallet** methods - read only data| |*wallet:read_write*|Access to **wallet** methods - allows to withdraw, generate new deposit address, etc.| |*wallet:none*, *account:none*, *trade:none*|Blocked access to specified functionality|    <span style=\"color:red\">**NOTICE:**</span> Depending on choosing an authentication method (```grant type```) some scopes could be narrowed by the server. e.g. when ```grant_type = client_credentials``` and ```scope = wallet:read_write``` it's modified by the server as ```scope = wallet:read```\"   ## JSON-RPC over websocket Websocket is the prefered transport mechanism for the JSON-RPC API, because it is faster and because it can support [subscriptions](#subscriptions) and [cancel on disconnect](#private-enable_cancel_on_disconnect). The code examples that can be found next to each of the methods show how websockets can be used from Python or Javascript/node.js.  ## JSON-RPC over HTTP Besides websockets it is also possible to use the API via HTTP. The code examples for 'shell' show how this can be done using curl. Note that subscriptions and cancel on disconnect are not supported via HTTP.  #Methods 

The version of the OpenAPI document: 2.0.0

Generated by: https://openapi-generator.tech
OpenAPI Generator version: 4.0.2-SNAPSHOT

=end

require 'uri'
require 'cgi'

module OpenapiClient
  class AccountManagementApi
    attr_accessor :api_client

    def initialize(api_client = ApiClient.default)
      @api_client = api_client
    end
    # Change the user name for a subaccount
    # @param sid [Integer] The user id for the subaccount
    # @param name [String] The new user name
    # @param [Hash] opts the optional parameters
    # @return [Object]
    def private_change_subaccount_name_get(sid, name, opts = {})
      data, _status_code, _headers = private_change_subaccount_name_get_with_http_info(sid, name, opts)
      data
    end

    # Change the user name for a subaccount
    # @param sid [Integer] The user id for the subaccount
    # @param name [String] The new user name
    # @param [Hash] opts the optional parameters
    # @return [Array<(Object, Integer, Hash)>] Object data, response status code and response headers
    def private_change_subaccount_name_get_with_http_info(sid, name, opts = {})
      if @api_client.config.debugging
        @api_client.config.logger.debug 'Calling API: AccountManagementApi.private_change_subaccount_name_get ...'
      end
      # verify the required parameter 'sid' is set
      if @api_client.config.client_side_validation && sid.nil?
        fail ArgumentError, "Missing the required parameter 'sid' when calling AccountManagementApi.private_change_subaccount_name_get"
      end
      # verify the required parameter 'name' is set
      if @api_client.config.client_side_validation && name.nil?
        fail ArgumentError, "Missing the required parameter 'name' when calling AccountManagementApi.private_change_subaccount_name_get"
      end
      # resource path
      local_var_path = '/private/change_subaccount_name'

      # query parameters
      query_params = opts[:query_params] || {}
      query_params[:'sid'] = sid
      query_params[:'name'] = name

      # header parameters
      header_params = opts[:header_params] || {}
      # HTTP header 'Accept' (if needed)
      header_params['Accept'] = @api_client.select_header_accept(['application/json'])

      # form parameters
      form_params = opts[:form_params] || {}

      # http body (model)
      post_body = opts[:body] 

      # return_type
      return_type = opts[:return_type] || 'Object' 

      # auth_names
      auth_names = opts[:auth_names] || ['bearerAuth']

      new_options = opts.merge(
        :header_params => header_params,
        :query_params => query_params,
        :form_params => form_params,
        :body => post_body,
        :auth_names => auth_names,
        :return_type => return_type
      )

      data, status_code, headers = @api_client.call_api(:GET, local_var_path, new_options)
      if @api_client.config.debugging
        @api_client.config.logger.debug "API called: AccountManagementApi#private_change_subaccount_name_get\nData: #{data.inspect}\nStatus code: #{status_code}\nHeaders: #{headers}"
      end
      return data, status_code, headers
    end

    # Create a new subaccount
    # @param [Hash] opts the optional parameters
    # @return [Object]
    def private_create_subaccount_get(opts = {})
      data, _status_code, _headers = private_create_subaccount_get_with_http_info(opts)
      data
    end

    # Create a new subaccount
    # @param [Hash] opts the optional parameters
    # @return [Array<(Object, Integer, Hash)>] Object data, response status code and response headers
    def private_create_subaccount_get_with_http_info(opts = {})
      if @api_client.config.debugging
        @api_client.config.logger.debug 'Calling API: AccountManagementApi.private_create_subaccount_get ...'
      end
      # resource path
      local_var_path = '/private/create_subaccount'

      # query parameters
      query_params = opts[:query_params] || {}

      # header parameters
      header_params = opts[:header_params] || {}
      # HTTP header 'Accept' (if needed)
      header_params['Accept'] = @api_client.select_header_accept(['application/json'])

      # form parameters
      form_params = opts[:form_params] || {}

      # http body (model)
      post_body = opts[:body] 

      # return_type
      return_type = opts[:return_type] || 'Object' 

      # auth_names
      auth_names = opts[:auth_names] || ['bearerAuth']

      new_options = opts.merge(
        :header_params => header_params,
        :query_params => query_params,
        :form_params => form_params,
        :body => post_body,
        :auth_names => auth_names,
        :return_type => return_type
      )

      data, status_code, headers = @api_client.call_api(:GET, local_var_path, new_options)
      if @api_client.config.debugging
        @api_client.config.logger.debug "API called: AccountManagementApi#private_create_subaccount_get\nData: #{data.inspect}\nStatus code: #{status_code}\nHeaders: #{headers}"
      end
      return data, status_code, headers
    end

    # Disable two factor authentication for a subaccount.
    # @param sid [Integer] The user id for the subaccount
    # @param [Hash] opts the optional parameters
    # @return [Object]
    def private_disable_tfa_for_subaccount_get(sid, opts = {})
      data, _status_code, _headers = private_disable_tfa_for_subaccount_get_with_http_info(sid, opts)
      data
    end

    # Disable two factor authentication for a subaccount.
    # @param sid [Integer] The user id for the subaccount
    # @param [Hash] opts the optional parameters
    # @return [Array<(Object, Integer, Hash)>] Object data, response status code and response headers
    def private_disable_tfa_for_subaccount_get_with_http_info(sid, opts = {})
      if @api_client.config.debugging
        @api_client.config.logger.debug 'Calling API: AccountManagementApi.private_disable_tfa_for_subaccount_get ...'
      end
      # verify the required parameter 'sid' is set
      if @api_client.config.client_side_validation && sid.nil?
        fail ArgumentError, "Missing the required parameter 'sid' when calling AccountManagementApi.private_disable_tfa_for_subaccount_get"
      end
      # resource path
      local_var_path = '/private/disable_tfa_for_subaccount'

      # query parameters
      query_params = opts[:query_params] || {}
      query_params[:'sid'] = sid

      # header parameters
      header_params = opts[:header_params] || {}
      # HTTP header 'Accept' (if needed)
      header_params['Accept'] = @api_client.select_header_accept(['application/json'])

      # form parameters
      form_params = opts[:form_params] || {}

      # http body (model)
      post_body = opts[:body] 

      # return_type
      return_type = opts[:return_type] || 'Object' 

      # auth_names
      auth_names = opts[:auth_names] || ['bearerAuth']

      new_options = opts.merge(
        :header_params => header_params,
        :query_params => query_params,
        :form_params => form_params,
        :body => post_body,
        :auth_names => auth_names,
        :return_type => return_type
      )

      data, status_code, headers = @api_client.call_api(:GET, local_var_path, new_options)
      if @api_client.config.debugging
        @api_client.config.logger.debug "API called: AccountManagementApi#private_disable_tfa_for_subaccount_get\nData: #{data.inspect}\nStatus code: #{status_code}\nHeaders: #{headers}"
      end
      return data, status_code, headers
    end

    # Retrieves user account summary.
    # @param currency [String] The currency symbol
    # @param [Hash] opts the optional parameters
    # @option opts [Boolean] :extended Include additional fields
    # @return [Object]
    def private_get_account_summary_get(currency, opts = {})
      data, _status_code, _headers = private_get_account_summary_get_with_http_info(currency, opts)
      data
    end

    # Retrieves user account summary.
    # @param currency [String] The currency symbol
    # @param [Hash] opts the optional parameters
    # @option opts [Boolean] :extended Include additional fields
    # @return [Array<(Object, Integer, Hash)>] Object data, response status code and response headers
    def private_get_account_summary_get_with_http_info(currency, opts = {})
      if @api_client.config.debugging
        @api_client.config.logger.debug 'Calling API: AccountManagementApi.private_get_account_summary_get ...'
      end
      # verify the required parameter 'currency' is set
      if @api_client.config.client_side_validation && currency.nil?
        fail ArgumentError, "Missing the required parameter 'currency' when calling AccountManagementApi.private_get_account_summary_get"
      end
      # verify enum value
      allowable_values = ["BTC", "ETH"]
      if @api_client.config.client_side_validation && !allowable_values.include?(currency)
        fail ArgumentError, "invalid value for \"currency\", must be one of #{allowable_values}"
      end
      # resource path
      local_var_path = '/private/get_account_summary'

      # query parameters
      query_params = opts[:query_params] || {}
      query_params[:'currency'] = currency
      query_params[:'extended'] = opts[:'extended'] if !opts[:'extended'].nil?

      # header parameters
      header_params = opts[:header_params] || {}
      # HTTP header 'Accept' (if needed)
      header_params['Accept'] = @api_client.select_header_accept(['application/json'])

      # form parameters
      form_params = opts[:form_params] || {}

      # http body (model)
      post_body = opts[:body] 

      # return_type
      return_type = opts[:return_type] || 'Object' 

      # auth_names
      auth_names = opts[:auth_names] || ['bearerAuth']

      new_options = opts.merge(
        :header_params => header_params,
        :query_params => query_params,
        :form_params => form_params,
        :body => post_body,
        :auth_names => auth_names,
        :return_type => return_type
      )

      data, status_code, headers = @api_client.call_api(:GET, local_var_path, new_options)
      if @api_client.config.debugging
        @api_client.config.logger.debug "API called: AccountManagementApi#private_get_account_summary_get\nData: #{data.inspect}\nStatus code: #{status_code}\nHeaders: #{headers}"
      end
      return data, status_code, headers
    end

    # Retrieves the language to be used for emails.
    # @param [Hash] opts the optional parameters
    # @return [Object]
    def private_get_email_language_get(opts = {})
      data, _status_code, _headers = private_get_email_language_get_with_http_info(opts)
      data
    end

    # Retrieves the language to be used for emails.
    # @param [Hash] opts the optional parameters
    # @return [Array<(Object, Integer, Hash)>] Object data, response status code and response headers
    def private_get_email_language_get_with_http_info(opts = {})
      if @api_client.config.debugging
        @api_client.config.logger.debug 'Calling API: AccountManagementApi.private_get_email_language_get ...'
      end
      # resource path
      local_var_path = '/private/get_email_language'

      # query parameters
      query_params = opts[:query_params] || {}

      # header parameters
      header_params = opts[:header_params] || {}
      # HTTP header 'Accept' (if needed)
      header_params['Accept'] = @api_client.select_header_accept(['application/json'])

      # form parameters
      form_params = opts[:form_params] || {}

      # http body (model)
      post_body = opts[:body] 

      # return_type
      return_type = opts[:return_type] || 'Object' 

      # auth_names
      auth_names = opts[:auth_names] || ['bearerAuth']

      new_options = opts.merge(
        :header_params => header_params,
        :query_params => query_params,
        :form_params => form_params,
        :body => post_body,
        :auth_names => auth_names,
        :return_type => return_type
      )

      data, status_code, headers = @api_client.call_api(:GET, local_var_path, new_options)
      if @api_client.config.debugging
        @api_client.config.logger.debug "API called: AccountManagementApi#private_get_email_language_get\nData: #{data.inspect}\nStatus code: #{status_code}\nHeaders: #{headers}"
      end
      return data, status_code, headers
    end

    # Retrieves announcements that have not been marked read by the user.
    # @param [Hash] opts the optional parameters
    # @return [Object]
    def private_get_new_announcements_get(opts = {})
      data, _status_code, _headers = private_get_new_announcements_get_with_http_info(opts)
      data
    end

    # Retrieves announcements that have not been marked read by the user.
    # @param [Hash] opts the optional parameters
    # @return [Array<(Object, Integer, Hash)>] Object data, response status code and response headers
    def private_get_new_announcements_get_with_http_info(opts = {})
      if @api_client.config.debugging
        @api_client.config.logger.debug 'Calling API: AccountManagementApi.private_get_new_announcements_get ...'
      end
      # resource path
      local_var_path = '/private/get_new_announcements'

      # query parameters
      query_params = opts[:query_params] || {}

      # header parameters
      header_params = opts[:header_params] || {}
      # HTTP header 'Accept' (if needed)
      header_params['Accept'] = @api_client.select_header_accept(['application/json'])

      # form parameters
      form_params = opts[:form_params] || {}

      # http body (model)
      post_body = opts[:body] 

      # return_type
      return_type = opts[:return_type] || 'Object' 

      # auth_names
      auth_names = opts[:auth_names] || ['bearerAuth']

      new_options = opts.merge(
        :header_params => header_params,
        :query_params => query_params,
        :form_params => form_params,
        :body => post_body,
        :auth_names => auth_names,
        :return_type => return_type
      )

      data, status_code, headers = @api_client.call_api(:GET, local_var_path, new_options)
      if @api_client.config.debugging
        @api_client.config.logger.debug "API called: AccountManagementApi#private_get_new_announcements_get\nData: #{data.inspect}\nStatus code: #{status_code}\nHeaders: #{headers}"
      end
      return data, status_code, headers
    end

    # Retrieve user position.
    # @param instrument_name [String] Instrument name
    # @param [Hash] opts the optional parameters
    # @return [Object]
    def private_get_position_get(instrument_name, opts = {})
      data, _status_code, _headers = private_get_position_get_with_http_info(instrument_name, opts)
      data
    end

    # Retrieve user position.
    # @param instrument_name [String] Instrument name
    # @param [Hash] opts the optional parameters
    # @return [Array<(Object, Integer, Hash)>] Object data, response status code and response headers
    def private_get_position_get_with_http_info(instrument_name, opts = {})
      if @api_client.config.debugging
        @api_client.config.logger.debug 'Calling API: AccountManagementApi.private_get_position_get ...'
      end
      # verify the required parameter 'instrument_name' is set
      if @api_client.config.client_side_validation && instrument_name.nil?
        fail ArgumentError, "Missing the required parameter 'instrument_name' when calling AccountManagementApi.private_get_position_get"
      end
      # resource path
      local_var_path = '/private/get_position'

      # query parameters
      query_params = opts[:query_params] || {}
      query_params[:'instrument_name'] = instrument_name

      # header parameters
      header_params = opts[:header_params] || {}
      # HTTP header 'Accept' (if needed)
      header_params['Accept'] = @api_client.select_header_accept(['application/json'])

      # form parameters
      form_params = opts[:form_params] || {}

      # http body (model)
      post_body = opts[:body] 

      # return_type
      return_type = opts[:return_type] || 'Object' 

      # auth_names
      auth_names = opts[:auth_names] || ['bearerAuth']

      new_options = opts.merge(
        :header_params => header_params,
        :query_params => query_params,
        :form_params => form_params,
        :body => post_body,
        :auth_names => auth_names,
        :return_type => return_type
      )

      data, status_code, headers = @api_client.call_api(:GET, local_var_path, new_options)
      if @api_client.config.debugging
        @api_client.config.logger.debug "API called: AccountManagementApi#private_get_position_get\nData: #{data.inspect}\nStatus code: #{status_code}\nHeaders: #{headers}"
      end
      return data, status_code, headers
    end

    # Retrieve user positions.
    # @param currency [String] 
    # @param [Hash] opts the optional parameters
    # @option opts [String] :kind Kind filter on positions
    # @return [Object]
    def private_get_positions_get(currency, opts = {})
      data, _status_code, _headers = private_get_positions_get_with_http_info(currency, opts)
      data
    end

    # Retrieve user positions.
    # @param currency [String] 
    # @param [Hash] opts the optional parameters
    # @option opts [String] :kind Kind filter on positions
    # @return [Array<(Object, Integer, Hash)>] Object data, response status code and response headers
    def private_get_positions_get_with_http_info(currency, opts = {})
      if @api_client.config.debugging
        @api_client.config.logger.debug 'Calling API: AccountManagementApi.private_get_positions_get ...'
      end
      # verify the required parameter 'currency' is set
      if @api_client.config.client_side_validation && currency.nil?
        fail ArgumentError, "Missing the required parameter 'currency' when calling AccountManagementApi.private_get_positions_get"
      end
      # verify enum value
      allowable_values = ["BTC", "ETH"]
      if @api_client.config.client_side_validation && !allowable_values.include?(currency)
        fail ArgumentError, "invalid value for \"currency\", must be one of #{allowable_values}"
      end
      allowable_values = ["future", "option"]
      if @api_client.config.client_side_validation && opts[:'kind'] && !allowable_values.include?(opts[:'kind'])
        fail ArgumentError, "invalid value for \"kind\", must be one of #{allowable_values}"
      end
      # resource path
      local_var_path = '/private/get_positions'

      # query parameters
      query_params = opts[:query_params] || {}
      query_params[:'currency'] = currency
      query_params[:'kind'] = opts[:'kind'] if !opts[:'kind'].nil?

      # header parameters
      header_params = opts[:header_params] || {}
      # HTTP header 'Accept' (if needed)
      header_params['Accept'] = @api_client.select_header_accept(['application/json'])

      # form parameters
      form_params = opts[:form_params] || {}

      # http body (model)
      post_body = opts[:body] 

      # return_type
      return_type = opts[:return_type] || 'Object' 

      # auth_names
      auth_names = opts[:auth_names] || ['bearerAuth']

      new_options = opts.merge(
        :header_params => header_params,
        :query_params => query_params,
        :form_params => form_params,
        :body => post_body,
        :auth_names => auth_names,
        :return_type => return_type
      )

      data, status_code, headers = @api_client.call_api(:GET, local_var_path, new_options)
      if @api_client.config.debugging
        @api_client.config.logger.debug "API called: AccountManagementApi#private_get_positions_get\nData: #{data.inspect}\nStatus code: #{status_code}\nHeaders: #{headers}"
      end
      return data, status_code, headers
    end

    # Get information about subaccounts
    # @param [Hash] opts the optional parameters
    # @option opts [Boolean] :with_portfolio 
    # @return [Object]
    def private_get_subaccounts_get(opts = {})
      data, _status_code, _headers = private_get_subaccounts_get_with_http_info(opts)
      data
    end

    # Get information about subaccounts
    # @param [Hash] opts the optional parameters
    # @option opts [Boolean] :with_portfolio 
    # @return [Array<(Object, Integer, Hash)>] Object data, response status code and response headers
    def private_get_subaccounts_get_with_http_info(opts = {})
      if @api_client.config.debugging
        @api_client.config.logger.debug 'Calling API: AccountManagementApi.private_get_subaccounts_get ...'
      end
      # resource path
      local_var_path = '/private/get_subaccounts'

      # query parameters
      query_params = opts[:query_params] || {}
      query_params[:'with_portfolio'] = opts[:'with_portfolio'] if !opts[:'with_portfolio'].nil?

      # header parameters
      header_params = opts[:header_params] || {}
      # HTTP header 'Accept' (if needed)
      header_params['Accept'] = @api_client.select_header_accept(['application/json'])

      # form parameters
      form_params = opts[:form_params] || {}

      # http body (model)
      post_body = opts[:body] 

      # return_type
      return_type = opts[:return_type] || 'Object' 

      # auth_names
      auth_names = opts[:auth_names] || ['bearerAuth']

      new_options = opts.merge(
        :header_params => header_params,
        :query_params => query_params,
        :form_params => form_params,
        :body => post_body,
        :auth_names => auth_names,
        :return_type => return_type
      )

      data, status_code, headers = @api_client.call_api(:GET, local_var_path, new_options)
      if @api_client.config.debugging
        @api_client.config.logger.debug "API called: AccountManagementApi#private_get_subaccounts_get\nData: #{data.inspect}\nStatus code: #{status_code}\nHeaders: #{headers}"
      end
      return data, status_code, headers
    end

    # Marks an announcement as read, so it will not be shown in `get_new_announcements`.
    # @param announcement_id [Float] the ID of the announcement
    # @param [Hash] opts the optional parameters
    # @return [Object]
    def private_set_announcement_as_read_get(announcement_id, opts = {})
      data, _status_code, _headers = private_set_announcement_as_read_get_with_http_info(announcement_id, opts)
      data
    end

    # Marks an announcement as read, so it will not be shown in &#x60;get_new_announcements&#x60;.
    # @param announcement_id [Float] the ID of the announcement
    # @param [Hash] opts the optional parameters
    # @return [Array<(Object, Integer, Hash)>] Object data, response status code and response headers
    def private_set_announcement_as_read_get_with_http_info(announcement_id, opts = {})
      if @api_client.config.debugging
        @api_client.config.logger.debug 'Calling API: AccountManagementApi.private_set_announcement_as_read_get ...'
      end
      # verify the required parameter 'announcement_id' is set
      if @api_client.config.client_side_validation && announcement_id.nil?
        fail ArgumentError, "Missing the required parameter 'announcement_id' when calling AccountManagementApi.private_set_announcement_as_read_get"
      end
      # resource path
      local_var_path = '/private/set_announcement_as_read'

      # query parameters
      query_params = opts[:query_params] || {}
      query_params[:'announcement_id'] = announcement_id

      # header parameters
      header_params = opts[:header_params] || {}
      # HTTP header 'Accept' (if needed)
      header_params['Accept'] = @api_client.select_header_accept(['application/json'])

      # form parameters
      form_params = opts[:form_params] || {}

      # http body (model)
      post_body = opts[:body] 

      # return_type
      return_type = opts[:return_type] || 'Object' 

      # auth_names
      auth_names = opts[:auth_names] || ['bearerAuth']

      new_options = opts.merge(
        :header_params => header_params,
        :query_params => query_params,
        :form_params => form_params,
        :body => post_body,
        :auth_names => auth_names,
        :return_type => return_type
      )

      data, status_code, headers = @api_client.call_api(:GET, local_var_path, new_options)
      if @api_client.config.debugging
        @api_client.config.logger.debug "API called: AccountManagementApi#private_set_announcement_as_read_get\nData: #{data.inspect}\nStatus code: #{status_code}\nHeaders: #{headers}"
      end
      return data, status_code, headers
    end

    # Assign an email address to a subaccount. User will receive an email with confirmation link.
    # @param sid [Integer] The user id for the subaccount
    # @param email [String] The email address for the subaccount
    # @param [Hash] opts the optional parameters
    # @return [Object]
    def private_set_email_for_subaccount_get(sid, email, opts = {})
      data, _status_code, _headers = private_set_email_for_subaccount_get_with_http_info(sid, email, opts)
      data
    end

    # Assign an email address to a subaccount. User will receive an email with confirmation link.
    # @param sid [Integer] The user id for the subaccount
    # @param email [String] The email address for the subaccount
    # @param [Hash] opts the optional parameters
    # @return [Array<(Object, Integer, Hash)>] Object data, response status code and response headers
    def private_set_email_for_subaccount_get_with_http_info(sid, email, opts = {})
      if @api_client.config.debugging
        @api_client.config.logger.debug 'Calling API: AccountManagementApi.private_set_email_for_subaccount_get ...'
      end
      # verify the required parameter 'sid' is set
      if @api_client.config.client_side_validation && sid.nil?
        fail ArgumentError, "Missing the required parameter 'sid' when calling AccountManagementApi.private_set_email_for_subaccount_get"
      end
      # verify the required parameter 'email' is set
      if @api_client.config.client_side_validation && email.nil?
        fail ArgumentError, "Missing the required parameter 'email' when calling AccountManagementApi.private_set_email_for_subaccount_get"
      end
      # resource path
      local_var_path = '/private/set_email_for_subaccount'

      # query parameters
      query_params = opts[:query_params] || {}
      query_params[:'sid'] = sid
      query_params[:'email'] = email

      # header parameters
      header_params = opts[:header_params] || {}
      # HTTP header 'Accept' (if needed)
      header_params['Accept'] = @api_client.select_header_accept(['application/json'])

      # form parameters
      form_params = opts[:form_params] || {}

      # http body (model)
      post_body = opts[:body] 

      # return_type
      return_type = opts[:return_type] || 'Object' 

      # auth_names
      auth_names = opts[:auth_names] || ['bearerAuth']

      new_options = opts.merge(
        :header_params => header_params,
        :query_params => query_params,
        :form_params => form_params,
        :body => post_body,
        :auth_names => auth_names,
        :return_type => return_type
      )

      data, status_code, headers = @api_client.call_api(:GET, local_var_path, new_options)
      if @api_client.config.debugging
        @api_client.config.logger.debug "API called: AccountManagementApi#private_set_email_for_subaccount_get\nData: #{data.inspect}\nStatus code: #{status_code}\nHeaders: #{headers}"
      end
      return data, status_code, headers
    end

    # Changes the language to be used for emails.
    # @param language [String] The abbreviated language name. Valid values include &#x60;\&quot;en\&quot;&#x60;, &#x60;\&quot;ko\&quot;&#x60;, &#x60;\&quot;zh\&quot;&#x60;
    # @param [Hash] opts the optional parameters
    # @return [Object]
    def private_set_email_language_get(language, opts = {})
      data, _status_code, _headers = private_set_email_language_get_with_http_info(language, opts)
      data
    end

    # Changes the language to be used for emails.
    # @param language [String] The abbreviated language name. Valid values include &#x60;\&quot;en\&quot;&#x60;, &#x60;\&quot;ko\&quot;&#x60;, &#x60;\&quot;zh\&quot;&#x60;
    # @param [Hash] opts the optional parameters
    # @return [Array<(Object, Integer, Hash)>] Object data, response status code and response headers
    def private_set_email_language_get_with_http_info(language, opts = {})
      if @api_client.config.debugging
        @api_client.config.logger.debug 'Calling API: AccountManagementApi.private_set_email_language_get ...'
      end
      # verify the required parameter 'language' is set
      if @api_client.config.client_side_validation && language.nil?
        fail ArgumentError, "Missing the required parameter 'language' when calling AccountManagementApi.private_set_email_language_get"
      end
      # resource path
      local_var_path = '/private/set_email_language'

      # query parameters
      query_params = opts[:query_params] || {}
      query_params[:'language'] = language

      # header parameters
      header_params = opts[:header_params] || {}
      # HTTP header 'Accept' (if needed)
      header_params['Accept'] = @api_client.select_header_accept(['application/json'])

      # form parameters
      form_params = opts[:form_params] || {}

      # http body (model)
      post_body = opts[:body] 

      # return_type
      return_type = opts[:return_type] || 'Object' 

      # auth_names
      auth_names = opts[:auth_names] || ['bearerAuth']

      new_options = opts.merge(
        :header_params => header_params,
        :query_params => query_params,
        :form_params => form_params,
        :body => post_body,
        :auth_names => auth_names,
        :return_type => return_type
      )

      data, status_code, headers = @api_client.call_api(:GET, local_var_path, new_options)
      if @api_client.config.debugging
        @api_client.config.logger.debug "API called: AccountManagementApi#private_set_email_language_get\nData: #{data.inspect}\nStatus code: #{status_code}\nHeaders: #{headers}"
      end
      return data, status_code, headers
    end

    # Set the password for the subaccount
    # @param sid [Integer] The user id for the subaccount
    # @param password [String] The password for the subaccount
    # @param [Hash] opts the optional parameters
    # @return [Object]
    def private_set_password_for_subaccount_get(sid, password, opts = {})
      data, _status_code, _headers = private_set_password_for_subaccount_get_with_http_info(sid, password, opts)
      data
    end

    # Set the password for the subaccount
    # @param sid [Integer] The user id for the subaccount
    # @param password [String] The password for the subaccount
    # @param [Hash] opts the optional parameters
    # @return [Array<(Object, Integer, Hash)>] Object data, response status code and response headers
    def private_set_password_for_subaccount_get_with_http_info(sid, password, opts = {})
      if @api_client.config.debugging
        @api_client.config.logger.debug 'Calling API: AccountManagementApi.private_set_password_for_subaccount_get ...'
      end
      # verify the required parameter 'sid' is set
      if @api_client.config.client_side_validation && sid.nil?
        fail ArgumentError, "Missing the required parameter 'sid' when calling AccountManagementApi.private_set_password_for_subaccount_get"
      end
      # verify the required parameter 'password' is set
      if @api_client.config.client_side_validation && password.nil?
        fail ArgumentError, "Missing the required parameter 'password' when calling AccountManagementApi.private_set_password_for_subaccount_get"
      end
      # resource path
      local_var_path = '/private/set_password_for_subaccount'

      # query parameters
      query_params = opts[:query_params] || {}
      query_params[:'sid'] = sid
      query_params[:'password'] = password

      # header parameters
      header_params = opts[:header_params] || {}
      # HTTP header 'Accept' (if needed)
      header_params['Accept'] = @api_client.select_header_accept(['application/json'])

      # form parameters
      form_params = opts[:form_params] || {}

      # http body (model)
      post_body = opts[:body] 

      # return_type
      return_type = opts[:return_type] || 'Object' 

      # auth_names
      auth_names = opts[:auth_names] || ['bearerAuth']

      new_options = opts.merge(
        :header_params => header_params,
        :query_params => query_params,
        :form_params => form_params,
        :body => post_body,
        :auth_names => auth_names,
        :return_type => return_type
      )

      data, status_code, headers = @api_client.call_api(:GET, local_var_path, new_options)
      if @api_client.config.debugging
        @api_client.config.logger.debug "API called: AccountManagementApi#private_set_password_for_subaccount_get\nData: #{data.inspect}\nStatus code: #{status_code}\nHeaders: #{headers}"
      end
      return data, status_code, headers
    end

    # Enable or disable sending of notifications for the subaccount.
    # @param sid [Integer] The user id for the subaccount
    # @param state [Boolean] enable (&#x60;true&#x60;) or disable (&#x60;false&#x60;) notifications
    # @param [Hash] opts the optional parameters
    # @return [Object]
    def private_toggle_notifications_from_subaccount_get(sid, state, opts = {})
      data, _status_code, _headers = private_toggle_notifications_from_subaccount_get_with_http_info(sid, state, opts)
      data
    end

    # Enable or disable sending of notifications for the subaccount.
    # @param sid [Integer] The user id for the subaccount
    # @param state [Boolean] enable (&#x60;true&#x60;) or disable (&#x60;false&#x60;) notifications
    # @param [Hash] opts the optional parameters
    # @return [Array<(Object, Integer, Hash)>] Object data, response status code and response headers
    def private_toggle_notifications_from_subaccount_get_with_http_info(sid, state, opts = {})
      if @api_client.config.debugging
        @api_client.config.logger.debug 'Calling API: AccountManagementApi.private_toggle_notifications_from_subaccount_get ...'
      end
      # verify the required parameter 'sid' is set
      if @api_client.config.client_side_validation && sid.nil?
        fail ArgumentError, "Missing the required parameter 'sid' when calling AccountManagementApi.private_toggle_notifications_from_subaccount_get"
      end
      # verify the required parameter 'state' is set
      if @api_client.config.client_side_validation && state.nil?
        fail ArgumentError, "Missing the required parameter 'state' when calling AccountManagementApi.private_toggle_notifications_from_subaccount_get"
      end
      # resource path
      local_var_path = '/private/toggle_notifications_from_subaccount'

      # query parameters
      query_params = opts[:query_params] || {}
      query_params[:'sid'] = sid
      query_params[:'state'] = state

      # header parameters
      header_params = opts[:header_params] || {}
      # HTTP header 'Accept' (if needed)
      header_params['Accept'] = @api_client.select_header_accept(['application/json'])

      # form parameters
      form_params = opts[:form_params] || {}

      # http body (model)
      post_body = opts[:body] 

      # return_type
      return_type = opts[:return_type] || 'Object' 

      # auth_names
      auth_names = opts[:auth_names] || ['bearerAuth']

      new_options = opts.merge(
        :header_params => header_params,
        :query_params => query_params,
        :form_params => form_params,
        :body => post_body,
        :auth_names => auth_names,
        :return_type => return_type
      )

      data, status_code, headers = @api_client.call_api(:GET, local_var_path, new_options)
      if @api_client.config.debugging
        @api_client.config.logger.debug "API called: AccountManagementApi#private_toggle_notifications_from_subaccount_get\nData: #{data.inspect}\nStatus code: #{status_code}\nHeaders: #{headers}"
      end
      return data, status_code, headers
    end

    # Enable or disable login for a subaccount. If login is disabled and a session for the subaccount exists, this session will be terminated.
    # @param sid [Integer] The user id for the subaccount
    # @param state [String] enable or disable login.
    # @param [Hash] opts the optional parameters
    # @return [Object]
    def private_toggle_subaccount_login_get(sid, state, opts = {})
      data, _status_code, _headers = private_toggle_subaccount_login_get_with_http_info(sid, state, opts)
      data
    end

    # Enable or disable login for a subaccount. If login is disabled and a session for the subaccount exists, this session will be terminated.
    # @param sid [Integer] The user id for the subaccount
    # @param state [String] enable or disable login.
    # @param [Hash] opts the optional parameters
    # @return [Array<(Object, Integer, Hash)>] Object data, response status code and response headers
    def private_toggle_subaccount_login_get_with_http_info(sid, state, opts = {})
      if @api_client.config.debugging
        @api_client.config.logger.debug 'Calling API: AccountManagementApi.private_toggle_subaccount_login_get ...'
      end
      # verify the required parameter 'sid' is set
      if @api_client.config.client_side_validation && sid.nil?
        fail ArgumentError, "Missing the required parameter 'sid' when calling AccountManagementApi.private_toggle_subaccount_login_get"
      end
      # verify the required parameter 'state' is set
      if @api_client.config.client_side_validation && state.nil?
        fail ArgumentError, "Missing the required parameter 'state' when calling AccountManagementApi.private_toggle_subaccount_login_get"
      end
      # verify enum value
      allowable_values = ["enable", "disable"]
      if @api_client.config.client_side_validation && !allowable_values.include?(state)
        fail ArgumentError, "invalid value for \"state\", must be one of #{allowable_values}"
      end
      # resource path
      local_var_path = '/private/toggle_subaccount_login'

      # query parameters
      query_params = opts[:query_params] || {}
      query_params[:'sid'] = sid
      query_params[:'state'] = state

      # header parameters
      header_params = opts[:header_params] || {}
      # HTTP header 'Accept' (if needed)
      header_params['Accept'] = @api_client.select_header_accept(['application/json'])

      # form parameters
      form_params = opts[:form_params] || {}

      # http body (model)
      post_body = opts[:body] 

      # return_type
      return_type = opts[:return_type] || 'Object' 

      # auth_names
      auth_names = opts[:auth_names] || ['bearerAuth']

      new_options = opts.merge(
        :header_params => header_params,
        :query_params => query_params,
        :form_params => form_params,
        :body => post_body,
        :auth_names => auth_names,
        :return_type => return_type
      )

      data, status_code, headers = @api_client.call_api(:GET, local_var_path, new_options)
      if @api_client.config.debugging
        @api_client.config.logger.debug "API called: AccountManagementApi#private_toggle_subaccount_login_get\nData: #{data.inspect}\nStatus code: #{status_code}\nHeaders: #{headers}"
      end
      return data, status_code, headers
    end

    # Retrieves announcements from the last 30 days.
    # @param [Hash] opts the optional parameters
    # @return [Object]
    def public_get_announcements_get(opts = {})
      data, _status_code, _headers = public_get_announcements_get_with_http_info(opts)
      data
    end

    # Retrieves announcements from the last 30 days.
    # @param [Hash] opts the optional parameters
    # @return [Array<(Object, Integer, Hash)>] Object data, response status code and response headers
    def public_get_announcements_get_with_http_info(opts = {})
      if @api_client.config.debugging
        @api_client.config.logger.debug 'Calling API: AccountManagementApi.public_get_announcements_get ...'
      end
      # resource path
      local_var_path = '/public/get_announcements'

      # query parameters
      query_params = opts[:query_params] || {}

      # header parameters
      header_params = opts[:header_params] || {}
      # HTTP header 'Accept' (if needed)
      header_params['Accept'] = @api_client.select_header_accept(['application/json'])

      # form parameters
      form_params = opts[:form_params] || {}

      # http body (model)
      post_body = opts[:body] 

      # return_type
      return_type = opts[:return_type] || 'Object' 

      # auth_names
      auth_names = opts[:auth_names] || ['bearerAuth']

      new_options = opts.merge(
        :header_params => header_params,
        :query_params => query_params,
        :form_params => form_params,
        :body => post_body,
        :auth_names => auth_names,
        :return_type => return_type
      )

      data, status_code, headers = @api_client.call_api(:GET, local_var_path, new_options)
      if @api_client.config.debugging
        @api_client.config.logger.debug "API called: AccountManagementApi#public_get_announcements_get\nData: #{data.inspect}\nStatus code: #{status_code}\nHeaders: #{headers}"
      end
      return data, status_code, headers
    end
  end
end
