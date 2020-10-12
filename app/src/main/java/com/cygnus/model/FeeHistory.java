package com.cygnus.model;

public class FeeHistory {
    String ClassId,StudentName,FeeMonth,FeeDate,FeeAmount,Status,StudentRollno;

    public FeeHistory(String classId, String studentName, String feeMonth,
                      String feeDate, String feeAmount,String status,
                      String studentRollno) {
        this.setClassId(classId);
        this.setStudentName(studentName);
        this.setFeeMonth(feeMonth);
        this.setFeeDate(feeDate);
        this.setFeeAmount(feeAmount);
        this.setStatus(status);
        this.setStudentRollno(studentRollno);
    }

    public String getStudentRollno() {
        return StudentRollno;
    }

    public void setStudentRollno(String studentRollno) {
        StudentRollno = studentRollno;
    }

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String classId) {
        ClassId = classId;
    }

    public String getStudentName() {
        return StudentName;
    }

    public void setStudentName(String studentName) {
        StudentName = studentName;
    }

    public String getFeeMonth() {
        return FeeMonth;
    }

    public void setFeeMonth(String feeMonth) {
        FeeMonth = feeMonth;
    }

    public String getFeeDate() {
        return FeeDate;
    }

    public void setFeeDate(String feeDate) {
        FeeDate = feeDate;
    }

    public String getFeeAmount() {
        return FeeAmount;
    }

    public void setFeeAmount(String feeAmount) {
        FeeAmount = feeAmount;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
