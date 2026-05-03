<?xml version="1.0" encoding="UTF-8"?>
<!--
  Transform: ShipmentNotification → Invoice
  Actor:     Supplier
  Purpose:   Generate a VAT invoice from confirmed shipped goods; apply 10% tax.
             SubTotal is computed via XPath sum() over carried-through LineTotals.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

  <xsl:param name="newId"/>
  <xsl:param name="invoiceDate"/>
  <xsl:param name="dueDate"/>

  <xsl:template match="/">
    <xsl:variable name="subtotal"   select="sum(ShipmentNotification/ShippedItems/ShippedItem/LineTotal)"/>
    <xsl:variable name="taxAmount"  select="$subtotal * 0.10"/>
    <xsl:variable name="total"      select="$subtotal + $taxAmount"/>

    <Invoice>
      <InvoiceId><xsl:value-of select="$newId"/></InvoiceId>
      <OrderReference><xsl:value-of select="ShipmentNotification/OrderReference"/></OrderReference>
      <ShipmentReference><xsl:value-of select="ShipmentNotification/NotificationId"/></ShipmentReference>
      <InvoiceDate><xsl:value-of select="$invoiceDate"/></InvoiceDate>
      <DueDate><xsl:value-of select="$dueDate"/></DueDate>
      <PaymentTerms><xsl:value-of select="ShipmentNotification/PaymentTerms"/></PaymentTerms>
      <Currency><xsl:value-of select="ShipmentNotification/Currency"/></Currency>

      <BillTo>
        <BuyerId><xsl:value-of select="ShipmentNotification/BuyerId"/></BuyerId>
        <xsl:copy-of select="ShipmentNotification/DeliveryAddress"/>
      </BillTo>

      <BillFrom>
        <SupplierId><xsl:value-of select="ShipmentNotification/SupplierId"/></SupplierId>
        <BankAccount>IBAN-GB-SUPP001-12345678</BankAccount>
        <BankName>Global Trade Bank PLC</BankName>
        <BankBIC>GTBKGB21XXX</BankBIC>
      </BillFrom>

      <InvoiceLines>
        <xsl:for-each select="ShipmentNotification/ShippedItems/ShippedItem">
          <InvoiceLine>
            <LineNumber><xsl:value-of select="LineNumber"/></LineNumber>
            <ProductCode><xsl:value-of select="ProductCode"/></ProductCode>
            <Description><xsl:value-of select="Description"/></Description>
            <Quantity><xsl:value-of select="ShippedQuantity"/></Quantity>
            <UnitPrice><xsl:value-of select="UnitPrice"/></UnitPrice>
            <LineTotal><xsl:value-of select="LineTotal"/></LineTotal>
          </InvoiceLine>
        </xsl:for-each>
      </InvoiceLines>

      <SubTotal><xsl:value-of select="format-number($subtotal,  '#0.00')"/></SubTotal>
      <TaxRate>10</TaxRate>
      <TaxAmount><xsl:value-of select="format-number($taxAmount, '#0.00')"/></TaxAmount>
      <TotalAmount><xsl:value-of select="format-number($total,    '#0.00')"/></TotalAmount>
    </Invoice>
  </xsl:template>

</xsl:stylesheet>
