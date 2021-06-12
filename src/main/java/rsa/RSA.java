//PhanHuuDuy_18020015
package rsa;

import static java.math.BigInteger.TWO;

import java.math.BigInteger;
import java.util.Scanner;

public class RSA {

    static final BigInteger NEGATIVE_ONE = new BigInteger("-1");

    //cal  (a^b) % mod_
    public static BigInteger cal_pow(BigInteger a, BigInteger b, BigInteger mod_) {
        if (BigInteger.ZERO.equals(b)) {
            return BigInteger.ONE.mod(mod_);
        }
        if (BigInteger.ONE.equals(b)) {
            return a.mod(mod_);
        }
        BigInteger[] tmp = b.divideAndRemainder(TWO);
        BigInteger rs = cal_pow(a, tmp[0], mod_);
        rs = rs.multiply(rs).mod(mod_);
        if (tmp[1].equals(BigInteger.ONE)) {
            rs = rs.multiply(a).mod(mod_);
        }
        return rs;
    }

    //extend Euclid algorithm
    public static BigInteger[] ext_euclid(BigInteger a, BigInteger b) {
        BigInteger m = new BigInteger(a.toString());
        BigInteger n = new BigInteger(b.toString());
        BigInteger xm = BigInteger.ONE, ym = BigInteger.ZERO;
        BigInteger xn = BigInteger.ZERO, yn = BigInteger.ONE;

        BigInteger[] tmp;
        BigInteger q, r, xr, yr;
        while (!n.equals(BigInteger.ZERO)) {
            tmp = m.divideAndRemainder(n);
            q = tmp[0];
            r = tmp[1];
            xr = xm.subtract(q.multiply(xn));
            yr = ym.subtract(q.multiply(yn));

            m = new BigInteger(n.toString());

            xm = new BigInteger(xn.toString());
            ym = new BigInteger(yn.toString());

            n = new BigInteger(r.toString());

            xn = new BigInteger(xr.toString());
            yn = new BigInteger(yr.toString());
        }
        BigInteger[] rs = new BigInteger[2];
        rs[0] = xm;
        rs[1] = ym;
        return rs;
    }

    //cal  x^(-1) % mod_
    public static BigInteger module_inverse_euclid(BigInteger x, BigInteger mod_) {
        x = x.remainder(mod_).add(mod_).remainder(mod_);
        BigInteger[] tmp = ext_euclid(x, mod_);
        BigInteger a = new BigInteger(tmp[0].toString());
        BigInteger b = new BigInteger(tmp[1].toString());

        BigInteger ax = new BigInteger(a.multiply(x).toString());
        BigInteger by = new BigInteger(b.multiply(mod_).toString());
        if (ax.add(by).equals(BigInteger.ONE)) {
            return a.remainder(mod_).add(mod_).remainder(mod_);
        }
        return NEGATIVE_ONE;
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter p (prime):  ");
            BigInteger p = scanner.nextBigInteger();

            System.out.print("Enter q (prime):  ");
            BigInteger q = scanner.nextBigInteger();

            BigInteger n = new BigInteger(p.multiply(q).toString());
            System.out.println("n =  " + n);

            BigInteger phiN = new BigInteger(
                (p.subtract(BigInteger.ONE)).multiply((q.subtract(BigInteger.ONE))).toString());
            System.out.println("ETF(n) =  " + phiN + '\n');

            BigInteger e;
            do {
                System.out.print("Enter public key e (until gcd(e,ETF(n)) == 1):  ");
                e = scanner.nextBigInteger();
            } while (!phiN.gcd(e).equals(BigInteger.ONE));
            System.out.println();

            BigInteger d = new BigInteger(module_inverse_euclid(e, phiN).toString());

            System.out.println("public key  e =  " + e);
            System.out.println("->  private key  d =  " + d + '\n');

            if (d.equals(NEGATIVE_ONE)) {
                System.out.println("EXIT");
                return;
            }

            BigInteger msg;
            BigInteger encoded, decoded;
            while (true) {
                System.out
                    .print("Enter msg (hashed to number, must be less than 'n', -1 to stop) :  ");
                msg = scanner.nextBigInteger();
                if (msg.equals(NEGATIVE_ONE)) {
                    System.out.println("EXIT");
                    break;
                }

                encoded = new BigInteger(cal_pow(msg, e, n).toString());
                System.out.println("Encoded code =  msg^e =  " + encoded);

                decoded = new BigInteger(cal_pow(encoded, d, n).toString());
                System.out.println("Decoded code = encoded^d =  " + decoded);

                if (msg.equals(decoded)) {
                    System.out.println("->  TRUE:  msg == decoded\n");
                } else {
                    System.out.println("->  FALSE:  msg != decoded");
                    break;
                }
            }
        }
    }
}
