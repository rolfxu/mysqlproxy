import java.util.concurrent.CompletableFuture;

public class Snippet {
	public static void main(String[] args) {

	     String result = CompletableFuture.supplyAsync(() -> {
	         try {
	             Thread.sleep(4000);
	         } catch (InterruptedException e) {
	             e.printStackTrace();
	         }


	         return "hello ";
	     }).thenCombine(CompletableFuture.supplyAsync(() -> {
	         try {
	             Thread.sleep(3000);
	         } catch (InterruptedException e) {
	             e.printStackTrace();
	         }
	         System.out.println("return world...");  //会执行
	         return "world";
	     }), (s1, s2) -> {
	         String s = s1 + " " + s2;   //并不会执行
	         System.out.println("combine result :"+s); //并不会执行
	         return s;
	     }).whenComplete((s, t) -> {
	         System.out.println("current result is :" +s);
	         if(t != null){
	             System.out.println("阶段执行过程中存在异常：");
	             t.printStackTrace();
	         }
	     }).join();

	     System.out.println("final result:"+result); //并不会执行
	 
	}
}