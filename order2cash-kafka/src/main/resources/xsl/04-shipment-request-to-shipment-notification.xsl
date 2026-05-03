<?xml version="1.0" encoding="UTF-8"?>
<!--
  Transform: ShipmentRequest → ShipmentNotification
  Actor:     Logistics
  Purpose:   Confirm dispatch, assign tracking number, and notify supplier of shipment.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

  <xsl:param name="newId"/>
  <xsl:param name="shipmentDate"/>
  <xsl:param name="estimatedDeliveryDate"/>
  <xsl:param name="trackingNumber"/>

  <xsl:template match="/">
    <ShipmentNotification>
      <NotificationId><xsl:value-of select="$newId"/></NotificationId>
      <ShipmentRequestRef><xsl:value-of select="ShipmentRequest/RequestId"/></ShipmentRequestRef>
      <OrderReference><xsl:value-of select="ShipmentRequest/OrderReference"/></OrderReference>
      <SupplierId><xsl:value-of select="ShipmentRequest/SupplierId"/></SupplierId>
      <BuyerId><xsl:value-of select="ShipmentRequest/BuyerId"/></BuyerId>
      <ShipmentDate><xsl:value-of select="$shipmentDate"/></ShipmentDate>
      <EstimatedDeliveryDate><xsl:value-of select="$estimatedDeliveryDate"/></EstimatedDeliveryDate>
      <TrackingNumber><xsl:value-of select="$trackingNumber"/></TrackingNumber>
      <Carrier>FAST-FREIGHT EXPRESS</Carrier>
      <ShipmentStatus>DISPATCHED</ShipmentStatus>
      <PaymentTerms><xsl:value-of select="ShipmentRequest/PaymentTerms"/></PaymentTerms>
      <Currency><xsl:value-of select="ShipmentRequest/Currency"/></Currency>

      <xsl:copy-of select="ShipmentRequest/DeliveryAddress"/>

      <ShippedItems>
        <xsl:for-each select="ShipmentRequest/ShipItems/ShipItem">
          <ShippedItem>
            <LineNumber><xsl:value-of select="LineNumber"/></LineNumber>
            <ProductCode><xsl:value-of select="ProductCode"/></ProductCode>
            <Description><xsl:value-of select="Description"/></Description>
            <ShippedQuantity><xsl:value-of select="Quantity"/></ShippedQuantity>
            <UnitPrice><xsl:value-of select="UnitPrice"/></UnitPrice>
            <LineTotal><xsl:value-of select="LineTotal"/></LineTotal>
          </ShippedItem>
        </xsl:for-each>
      </ShippedItems>
    </ShipmentNotification>
  </xsl:template>

</xsl:stylesheet>
