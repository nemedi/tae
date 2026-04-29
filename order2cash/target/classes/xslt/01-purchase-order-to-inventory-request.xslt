<?xml version="1.0" encoding="UTF-8"?>
<!--
  Transform: PurchaseOrder → InventoryRequest
  Actor:     Supplier
  Purpose:   Extract line items from buyer's order and request stock availability check.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

  <xsl:param name="newId"/>
  <xsl:param name="currentDate"/>

  <xsl:template match="/">
    <InventoryRequest>
      <RequestId><xsl:value-of select="$newId"/></RequestId>
      <OrderReference><xsl:value-of select="PurchaseOrder/Header/OrderId"/></OrderReference>
      <SupplierId><xsl:value-of select="PurchaseOrder/Header/SupplierId"/></SupplierId>
      <BuyerId><xsl:value-of select="PurchaseOrder/Header/BuyerId"/></BuyerId>
      <RequestDate><xsl:value-of select="$currentDate"/></RequestDate>
      <PaymentTerms><xsl:value-of select="PurchaseOrder/Header/PaymentTerms"/></PaymentTerms>
      <Currency><xsl:value-of select="PurchaseOrder/Header/Currency"/></Currency>

      <!-- Carry delivery address through the chain so logistics can use it later -->
      <xsl:copy-of select="PurchaseOrder/DeliveryAddress"/>

      <RequestItems>
        <xsl:for-each select="PurchaseOrder/LineItems/LineItem">
          <RequestItem>
            <LineNumber><xsl:value-of select="LineNumber"/></LineNumber>
            <ProductCode><xsl:value-of select="ProductCode"/></ProductCode>
            <Description><xsl:value-of select="Description"/></Description>
            <RequiredQuantity><xsl:value-of select="Quantity"/></RequiredQuantity>
            <UnitPrice><xsl:value-of select="UnitPrice"/></UnitPrice>
            <LineTotal><xsl:value-of select="LineTotal"/></LineTotal>
          </RequestItem>
        </xsl:for-each>
      </RequestItems>
    </InventoryRequest>
  </xsl:template>

</xsl:stylesheet>
