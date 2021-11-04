package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidLocationException;

public class Position {
	private int aisleId=-1;
	private String rackId="";
	private int levelId=-1;
	
	public Position(String position){
		if(position == null || position.length() <= 0) {
			aisleId = -1;
			rackId = "";
			levelId = -1;
			return;
		}
		String[] fields = position.split("-");
		if(fields.length != 3) {
			throw new RuntimeException(new InvalidLocationException("Invalid position string: " + position));
		}
		if(fields[1].length() <= 0)
			throw new RuntimeException(new InvalidLocationException("Invalid position string: " + position));			
		try {
		aisleId = Integer.parseInt(fields[0]);
		rackId = fields[1];
		levelId = Integer.parseInt(fields[2]);
		}catch(Exception e) {
			throw new RuntimeException(new InvalidLocationException("Invalid position string: " + position));
		}
	}
	
	public int getAisleId() {
		return aisleId;
	}
	public String getRackId() {
		return rackId;
	}
	public int getLevelId() {
		return levelId;
	}

	@Override
	public String toString() {
		if(aisleId<=0 || rackId.equals("") || levelId <=0)
			return "";
		return ""+aisleId + "-" + rackId + "-" + levelId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		if (aisleId != other.aisleId)
			return false;
		if (levelId != other.levelId)
			return false;
		if (!rackId.equals(other.rackId))
			return false;
		return true;
	}	
	
}
