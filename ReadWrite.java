/*
 * ReadWrite.java
 *
 * Created on October 23, 2000, 8:05 PM
 */
 
//
// semaphore class code
//
import java.io.*;

class Semaphore {
	public Semaphore () {
		value = 0;
	}
	
	public Semaphore ( int v) {
		value = v;
	}
	
	public synchronized void P() {
		while ( value <= 0) {
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		value --;
	}

	public synchronized void P(long delay) throws FailedToPException {
	
	   while ( value <= 0) {
		try {
		     new TimerThread(delay,Thread.currentThread()).start();
		    wait();
		} catch (InterruptedException e) 
			{
				 throw new FailedToPException();
			}
				  
	   }
	   value --;
	}
	
	public synchronized void V() {
		++value;
		notify ();
	}
	
	private int value;
      }
				
class TimerThread extends Thread {
	public TimerThread( long time ,Thread thread){
		this.time = time;
		this.thread = thread;
	}
	public void run	() {
		try {
			sleep(time);
		} catch (Exception ignored) {}
	thread.interrupt();
	}
	
	private Thread thread;
	private long time;
}

//
// semaphore exception class
//
class SemException extends Exception {
	SemException (String message) {
		super (message);
	}
	SemException () {
		super ();
	}
}
class FailedToPException extends SemException {
	FailedToPException ( String message) {
		super (message);
	}
	FailedToPException () {
		super ();
	}
}


class reader extends Thread {
  private Semaphore S, mutex;
  private PrintStream pr;
  private int id;
  private volatile static int readCount;
  static {
   readCount = 0;
  }

  public reader(Semaphore mutex, Semaphore S,int id, PrintStream pr) {
    this.mutex = mutex;
    this.S = S;
    this.pr = pr;
    this.id = id;
  }
  
  public void run() {
    pr.println("Reader " + id + " trying to get into CR");
    mutex.P();
      readCount++;
      if ( readCount == 1) S.P();
    mutex.V();
    pr.println("Reader " + id + " is reading");
    try { 
      sleep((long)(Math.random() * 1000)); 
    }  catch (InterruptedException e) {};
    pr.println("Reader " + id + " is done reading");
    mutex.P();
      readCount--;
      if ( readCount == 0) S.V();
    mutex.V();
  } 
 }
 
class writer extends Thread {
  private Semaphore S;
  private int id;
  private PrintStream pr;
  
  public writer ( Semaphore S,int id, PrintStream pr) {
    this.S = S;
    this.id = id;
    this.pr = pr;
  }
  
  public void run() {
    pr.println("Writer " + id + " is trying to write");
    S.P();
    pr.println("Writer " + id + " is starting to write");
    try { 
      sleep((long)(Math.random() * 1000)); 
    }  catch (InterruptedException e) {};
    pr.println("Writer " + id + " is done writing");
    S.V();
    pr.println("Writer " + id + " is wrapping things up");
  }
}

  
public class ReadWrite extends Object {
  
  public static void main (String args[]) {
    int rn = 0;
    int wn = 0;
    Semaphore writeBlock = new Semaphore(1);
    Semaphore mutex = new Semaphore(1);
    
    for ( int i = 0; i < 9; i++) {
      if ( i % 2 == 1 ) 
          new reader(mutex,writeBlock,rn++,System.out).start();
      else  new writer(writeBlock,wn++,System.out).start(); 
    }  
  }
}