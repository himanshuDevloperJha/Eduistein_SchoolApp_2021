package com.cygnus.model;

public class ClassFees {
    String classId,feeType,feeCharges,recursiveFees;

    public ClassFees(String classId, String feeType, String feeCharges,String recursiveFees) {
        this.setClassId(classId);
        this.setFeeType(feeType);
        this.setFeeCharges(feeCharges);
        this.setRecursiveFees(recursiveFees);
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public String getFeeCharges() {
        return feeCharges;
    }

    public void setFeeCharges(String feeCharges) {
        this.feeCharges = feeCharges;
    }

    public String getRecursiveFees() {
        return recursiveFees;
    }

    public void setRecursiveFees(String recursiveFees) {
        this.recursiveFees = recursiveFees;
    }
}
