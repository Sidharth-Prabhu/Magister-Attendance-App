package com.frissco.magister;

public class ClassItem {
    public long getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public ClassItem(long cid, String className, String subjectName) {
        this.cid = cid;
        this.className = className;
        this.subjectName = subjectName;
    }

    private long cid;
    private String className;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    private String subjectName;

    public ClassItem(String className, String subjectName) {
        this.className = className;
        this.subjectName = subjectName;
    }
}
