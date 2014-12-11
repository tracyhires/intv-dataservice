package com.dendreon.intellivenge.dataservice;

public class JoinParameter {
	
	private String leftTableName;
	private String leftColumnName;
	private String rightTableName;
	private String rightColumnName;
	private JoinType joinType;
	
	public JoinParameter(String aLeftTableName, String aRightTableName, String aLeftColumnName, String aRightColumnName, JoinType aJoinType) {
		leftTableName = aLeftTableName;
		rightTableName = aRightTableName;
		leftColumnName = aLeftColumnName;
		rightColumnName = aRightColumnName;
		joinType = aJoinType;
	}

	public String getLeftTableName() {
		return leftTableName;
	}

	public void setLeftTableName(String leftTableName) {
		this.leftTableName = leftTableName;
	}

	public String getLeftColumnName() {
		return leftColumnName;
	}

	public void setLeftColumnName(String leftColumnName) {
		this.leftColumnName = leftColumnName;
	}

	public String getRightTableName() {
		return rightTableName;
	}

	public void setRightTableName(String rightTableName) {
		this.rightTableName = rightTableName;
	}

	public String getRightColumnName() {
		return rightColumnName;
	}

	public void setRightColumnName(String rightColumnName) {
		this.rightColumnName = rightColumnName;
	}

	public JoinType getJoinType() {
		return joinType;
	}

	public void setJoinType(JoinType joinType) {
		this.joinType = joinType;
	}
}
