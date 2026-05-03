<?xml version="1.0" encoding="UTF-8"?>
<!--
  Transform: PaymentInstruction → PaymentConfirmation
  Actor:     Bank
  Purpose:   Confirm successful fund transfer and issue a transaction reference.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

  <xsl:param name="newId"/>
  <xsl:param name="transactionId"/>
  <xsl:param name="confirmationDate"/>

  <xsl:template match="/">
    <PaymentConfirmation>
      <ConfirmationId><xsl:value-of select="$newId"/></ConfirmationId>
      <InstructionReference><xsl:value-of select="PaymentInstruction/InstructionId"/></InstructionReference>
      <InvoiceReference><xsl:value-of select="PaymentInstruction/InvoiceReference"/></InvoiceReference>
      <OrderReference><xsl:value-of select="PaymentInstruction/OrderReference"/></OrderReference>
      <TransactionId><xsl:value-of select="$transactionId"/></TransactionId>
      <ConfirmationDate><xsl:value-of select="$confirmationDate"/></ConfirmationDate>
      <Status>COMPLETED</Status>
      <ConfirmedAmount><xsl:value-of select="PaymentInstruction/Amount"/></ConfirmedAmount>
      <Currency><xsl:value-of select="PaymentInstruction/Currency"/></Currency>
      <PaymentReference><xsl:value-of select="PaymentInstruction/PaymentReference"/></PaymentReference>

      <PaidBy>
        <BuyerId><xsl:value-of select="PaymentInstruction/PayFrom/BuyerId"/></BuyerId>
      </PaidBy>

      <PaidTo>
        <SupplierId><xsl:value-of select="PaymentInstruction/PayTo/SupplierId"/></SupplierId>
        <TransactionReference><xsl:value-of select="$transactionId"/></TransactionReference>
      </PaidTo>
    </PaymentConfirmation>
  </xsl:template>

</xsl:stylesheet>
