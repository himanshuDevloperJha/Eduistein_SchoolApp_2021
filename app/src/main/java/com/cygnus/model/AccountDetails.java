package com.cygnus.model;

public class AccountDetails {
   // String accountno,holdername,ifsc,bankname,transactionId,transactionRefId,upiid;
    String upiid;

    public AccountDetails(String upiid) {
      this.setUpiid(upiid);
    }

    public String getUpiid() {
        return upiid;
    }

    public void setUpiid(String upiid) {
        this.upiid = upiid;
    }
}
