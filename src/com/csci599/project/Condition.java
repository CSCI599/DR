package com.csci599.project;



public class Condition {

	public static void main(String[] args) {
		int n = 10000;
		n = n + 4;
		int a = Integer.parseInt(args[0]), b = Integer.parseInt(args[1]), c = Integer
				.parseInt(args[2]), d = Integer.parseInt(args[3]), e = Integer
				.parseInt(args[4]), f = Integer.parseInt(args[5]), g = Integer
				.parseInt(args[6]), h = Integer.parseInt(args[7]);
		String x1 = "xy";
		if (b == 3 && c == a && x1.equalsIgnoreCase("xy")) {
			if (d == 5) {
				if (a == 6) {
					System.out.println("A = 6");
				}
				if (a == 7) {
					System.out.println("A != 6");
					System.out.println("This is the line to execute");
				}
			} else {
				if (a == 7) {
					System.out.println("A = 7");
				} else {
					System.out.println("A != 7");
				}
			}
		} else {
			if (e == 8) {
				System.out.println("E = 8");
			} else {
				System.out.println("E != 8");
			}
		}

		if (f >= 3 && g <= 4) {
			if (h == 5) {
				e = n + 1 * (a / 4);
				if (e == 6) {
					System.out.println("E = 6");
				} else {
					System.out.println("E != 6");
				}
			} else {
				if (e == 7) {
					System.out.println("E = 7");
				} else {
					System.out.println("E != 7");
				}
			}
		} else {
			if (e == 8) {
				System.out.println("E = 8");
			} else {
				System.out.println("E != 8");
			}
		}
		System.out.println("Start of WHILE Loop");
		int x = 1;
		while (x < 10) {
			System.out.println("This is X WHILE: " + x);
			x++;
		}

		x = 1;
		System.out.println("Start of DO WHILE Loop");
		do {
			System.out.println("This is X DO WHILE: " + x);
			x++;
		} while (x < 10);

		System.out.println("Start of FOR Loop");
		for (x = 1; x < 10; x++) {
			System.out.println("This is X FOR: " + x);
			if (x == 8) {
				break;
			}
		}
		System.out.println("This is the end");
		char ch = '0';
		switch(ch){
		case '0':
			System.out.println("ch = 0");
			break;
		case '1':
			System.out.println("ch = 1");
			break;
		case '2':
			System.out.println("ch = 2");
			break;
			
		}
	}
}
