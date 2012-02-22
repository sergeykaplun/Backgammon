package com.backgammon;

import java.util.Stack;

public class Journal{
	private Stack<CompleteMove> journal;
	private static Journal instance; 
	
	public static Journal getInstance(){
		if(instance == null)
			instance = new Journal();
		return instance;
	}
	private Journal(){
		journal = new Stack<CompleteMove>();
	}
	public void put(CompleteMove move){
		journal.push(move);
	}
	public CompleteMove get(){
		if(journal.size()>0)
			return journal.pop();
		else 
			return null;
	}
	public boolean isClear(){
		return (journal.size()>0)?false:true;
	}
	public void clear(){
		journal.clear();
	}
}