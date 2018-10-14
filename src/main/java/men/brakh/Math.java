package men.brakh;

public class Math {
    public static int pow(int x, int n, int mod) {
        int res = 1;
        for (long p = x; n > 0; n >>= 1, p = (p * p) % mod) {
            if ((n & 1) != 0) {
                res = (int) (res * p % mod);
            }
        }
        return res;
    }
    //  return array [d, a, b] such that d = gcd(p, q), ap + bq = d
    static int[] gcd(int p, int q) {
        if (q == 0)
            return new int[] { p, 1, 0 };

        int[] vals = gcd(q, p % q);
        int d = vals[0];
        int a = vals[2];
        int b = vals[1] - (p / q) * vals[2];
        return new int[] { d, a, b };
    }
    static int[] Euclid_Extended(int a,int b){
        int d0 = a;
        int d1 = b;
        int x0 = 1;
        int x1 = 0;
        int y0 = 0;
        int y1 = 1;
        int d2,q,x2,y2;
        while (d1>1){
            q = d0/d1;
            d2 = d0%d1;
            x2 = x0-q*x1;
            y2 = y0-q*y1;
            d0 = d1; d1 = d2;
            x0 = x1; x1=x2;
            y0 = y1; y1=y2;
        }
        return new int[]{x1,y1,d1};
    }

    public static int phi (int n) {
        int result = n;
        for (int i=2; i*i<=n; ++i)
            if (n % i == 0) {
                while (n % i == 0)
                    n /= i;
                result -= result / i;
            }
        if (n > 1)
            result -= result / n;
        return result;
    }
    public static long gcd(long a, long b){
        if(b==0)
            return a;
        return gcd(b, a%b);
    }
    public static boolean CheckSimple(int p,int q){
        int arr[];
        arr = Math.gcd(p,q);
        return arr[0] == 1;
    }
    static int EulerFunc(int a,int b){
        return (a-1)*(b-1);
    }
}
