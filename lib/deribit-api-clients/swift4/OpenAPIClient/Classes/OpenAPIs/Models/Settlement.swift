//
// Settlement.swift
//
// Generated by openapi-generator
// https://openapi-generator.tech
//

import Foundation



public struct Settlement: Codable {

    public enum ModelType: String, Codable {
        case settlement = "settlement"
        case delivery = "delivery"
        case bankruptcy = "bankruptcy"
    }
    /** total value of session profit and losses (in base currency) */
    public var sessionProfitLoss: Double
    /** mark price for at the settlement time (in quote currency; settlement and delivery only) */
    public var markPrice: Double?
    /** funding (in base currency ; settlement for perpetual product only) */
    public var funding: Double
    /** the amount of the socialized losses (in base currency; bankruptcy only) */
    public var socialized: Double?
    /** value of session bankrupcy (in base currency; bankruptcy only) */
    public var sessionBankrupcy: Double?
    /** The timestamp (seconds since the Unix epoch, with millisecond precision) */
    public var timestamp: Int
    /** profit and loss (in base currency; settlement and delivery only) */
    public var profitLoss: Double?
    /** funded amount (bankruptcy only) */
    public var funded: Double?
    /** underlying index price at time of event (in quote currency; settlement and delivery only) */
    public var indexPrice: Double
    /** total amount of paid taxes/fees (in base currency; bankruptcy only) */
    public var sessionTax: Double?
    /** rate of paid texes/fees (in base currency; bankruptcy only) */
    public var sessionTaxRate: Double?
    /** instrument name (settlement and delivery only) */
    public var instrumentName: String
    /** position size (in quote currency; settlement and delivery only) */
    public var position: Double
    /** The type of settlement. &#x60;settlement&#x60;, &#x60;delivery&#x60; or &#x60;bankruptcy&#x60;. */
    public var type: ModelType

    public init(sessionProfitLoss: Double, markPrice: Double?, funding: Double, socialized: Double?, sessionBankrupcy: Double?, timestamp: Int, profitLoss: Double?, funded: Double?, indexPrice: Double, sessionTax: Double?, sessionTaxRate: Double?, instrumentName: String, position: Double, type: ModelType) {
        self.sessionProfitLoss = sessionProfitLoss
        self.markPrice = markPrice
        self.funding = funding
        self.socialized = socialized
        self.sessionBankrupcy = sessionBankrupcy
        self.timestamp = timestamp
        self.profitLoss = profitLoss
        self.funded = funded
        self.indexPrice = indexPrice
        self.sessionTax = sessionTax
        self.sessionTaxRate = sessionTaxRate
        self.instrumentName = instrumentName
        self.position = position
        self.type = type
    }

    public enum CodingKeys: String, CodingKey { 
        case sessionProfitLoss = "session_profit_loss"
        case markPrice = "mark_price"
        case funding
        case socialized
        case sessionBankrupcy = "session_bankrupcy"
        case timestamp
        case profitLoss = "profit_loss"
        case funded
        case indexPrice = "index_price"
        case sessionTax = "session_tax"
        case sessionTaxRate = "session_tax_rate"
        case instrumentName = "instrument_name"
        case position
        case type
    }


}

