package com.garry.springlifecycle.container.annotation.type;


import com.garry.springlifecycle.container.annotation.AnnotationUtil;
import com.garry.springlifecycle.controller.context.AppContextWrapper;
import com.garry.springlifecycle.utils.scanannotation.ScanAnnotationDB;

import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class AnnotationScaner {

	private ScanAnnotationDB db;

	private FutureTask<ScanAnnotationDB> ft;

	public Map<String, Set<String>> getScannedAnnotations(AppContextWrapper context) {
		if (db != null)
			return db.getAnnotationIndex();

		try {
			db = ft.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return db.getAnnotationIndex();
	}

	public void startScan(final AppContextWrapper context) {

		this.ft = new FutureTask(new Callable<ScanAnnotationDB>() {
			public ScanAnnotationDB call() throws Exception {
				ScanAnnotationDB db = new ScanAnnotationDB();
				URL[] urls = AnnotationUtil.scanAnnotation(context);
				try {
					db.scanArchives(urls);
				} catch (Exception e) {
					System.err.print("[JdonFramework] scanAnnotation error:" + e);
				}
				return db;
			}
		});
		ft.run();

	}

}
