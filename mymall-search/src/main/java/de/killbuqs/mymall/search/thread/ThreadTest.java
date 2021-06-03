package de.killbuqs.mymall.search.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class ThreadTest {

	public static ExecutorService executor = Executors.newFixedThreadPool(10);

	public static void main1(String[] args) throws InterruptedException, ExecutionException {

//		1. 启动线程  通过Callable FutureTask
//		FutureTask<Integer> futureTask = new FutureTask<>(() -> 0);
//		new Thread(futureTask).start();

//		2. 通过线程池 

//		3. CompletableFuture
//		启动一个异步任务 
//		CompletableFuture<Void> runAsync(Runnable runnable);
//		CompletableFuture<U> supplyAsync(Supplier<U> supplier)

//		CompletableFuture.runAsync(() -> {
//			System.out.println("Current thread name: " + Thread.currentThread().getName());
//		}, executor);

//		CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//			System.out.println("Current thread name: " + Thread.currentThread().getName());
//			return 0;
//		}, executor);
//		Integer integer = future.get();
//		System.out.println("Result: " + integer);

		// ============================
//		whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action);
//		exceptionally(Function<Throwable, ? extends T> fn);

//		CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//			System.out.println("Current thread name: " + Thread.currentThread().getName());
//			return 0;
//		}, executor).whenCompleteAsync((res, exception) -> {
//			// 出现异常只能感知，不能修改返回结果
//			System.out.println("Asynchron task done and the result is : " + res);
//		}).exceptionally(throwable -> {
//			// 感知异常，同时返回默认值 
//			return 10;
//		});
//		System.out.println("main...end..."+ future.get()) ;

//				

	}

	public static void main2(String[] args) throws InterruptedException, ExecutionException {
		
//		线程串行化
//		thenApplyAsync(Function)
//		thenAcceptAsync(Consumer)
//		thenRunAsync(Runnable)
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
			System.out.println("Current thread name: " + Thread.currentThread().getName());
			return "first";
		}, executor).thenApplyAsync( item -> {
			return item + " second";
		}, executor);
		
		System.out.println(future.get());

		
	}
	
	public static void main3(String[] args) throws InterruptedException, ExecutionException {
		
//		两任务组合, 完成两任务后，操作第三任务
//		runAfterBothAsync(CompletionStage, Runnable, Executor)
//		thenAcceptBothAsync(CompletionStage, BiConsumer, Executor)
//		thenCombineAsync(CompletionStage, BiFunction, Executor)
		CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
			System.out.println("Current thread name: " + Thread.currentThread().getName());
			return "first";
		}, executor);
		CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
			System.out.println("Current thread name: " + Thread.currentThread().getName());
			return "second";
		}, executor);
		
		CompletableFuture<String> future3 = future1.thenCombineAsync(future2, (u ,v) -> {
			return u + v + "three"; 
		}, executor);
		
		System.out.println(future3.get());
		
		
	}
	
	public static void main4(String[] args) throws InterruptedException, ExecutionException {
		
//		两任务组合, 只需要一个完成，操作第三任务
//		runAfterEitherAsync(CompletionStage, Runnable, Executor)
//		acceptBothAsync(CompletionStage, BiConsumer, Executor)
//		applyToEitherAsync(CompletionStage, BiFunction, Executor)
		CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
			System.out.println("Current thread name: " + Thread.currentThread().getName());
			return "first";
		}, executor);
		CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
			System.out.println("Current thread name: " + Thread.currentThread().getName());
			return "second";
		}, executor);
		
//		CompletableFuture<Void> future3 = future1.runAfterEitherAsync(future2, () -> {
//		}, executor);

		CompletableFuture<String> future4 = future1.applyToEitherAsync(future2, (u) -> {
			return "four";
		}, executor);
		
		System.out.println(future4.get());
		
	}
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
//		多任务组合
//		allOf(CompletionStage, Runnable, Executor)
//		anyOf(CompletionStage, BiConsumer, Executor)
		CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
			System.out.println("Current thread name: " + Thread.currentThread().getName());
			return "first";
		}, executor);
		CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Current thread name: " + Thread.currentThread().getName());
			return "second";
		}, executor);
		
		CompletableFuture<Void> allOf = CompletableFuture.allOf(future1, future2);
		allOf.get();
		
		CompletableFuture<Object> anyOf = CompletableFuture.anyOf(future1, future2);
		anyOf.get(); // 得到成功的那个
		
		System.out.println("main end");
		
	}

}
