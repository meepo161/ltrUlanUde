package ru.avem.posum.utils;

public class FFT {
    public static double amplitude8Hz = 0;
    public static double amplitude20Hz = 0;

    // compute the FFT of x[], assuming its length is a power of 2
    public static Complex[] fft(Complex[] x) {
        int n = x.length;

        // base case
        if (n == 1) return new Complex[] { x[0] };

        // radix 2 Cooley-Tukey FFT
        if (n % 2 != 0) {
            throw new IllegalArgumentException("n is not a power of 2");
        }

        // fft of even terms
        Complex[] even = new Complex[n/2];
        for (int k = 0; k < n/2; k++) {
            even[k] = x[2*k];
        }
        Complex[] q = fft(even);

        // fft of odd terms
        Complex[] odd  = even;  // reuse the array
        for (int k = 0; k < n/2; k++) {
            odd[k] = x[2*k + 1];
        }
        Complex[] r = fft(odd);

        // combine
        Complex[] y = new Complex[n];
        for (int k = 0; k < n/2; k++) {
            double kth = -2 * k * Math.PI / n;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[k]       = q[k].plus(wk.times(r[k]));
            y[k + n/2] = q[k].minus(wk.times(r[k]));
        }
        return y;
    }


    // compute the inverse FFT of x[], assuming its length is a power of 2
    public static Complex[] ifft(Complex[] x) {
        int n = x.length;
        Complex[] y = new Complex[n];

        // take conjugate
        for (int i = 0; i < n; i++) {
            y[i] = x[i].conjugate();
        }

        // compute forward FFT
        y = fft(y);

        // take conjugate again
        for (int i = 0; i < n; i++) {
            y[i] = y[i].conjugate();
        }

        // divide by n
        for (int i = 0; i < n; i++) {
            y[i] = y[i].scale(1.0 / n);
        }

        return y;

    }

    // compute the circular convolution of x and y
    public static Complex[] cconvolve(Complex[] x, Complex[] y) {

        // should probably pad x and y with 0s so that they have same length
        // and are powers of 2
        if (x.length != y.length) {
            throw new IllegalArgumentException("Dimensions don't agree");
        }

        int n = x.length;

        // compute FFT of each sequence
        Complex[] a = fft(x);
        Complex[] b = fft(y);

        // point-wise multiply
        Complex[] c = new Complex[n];
        for (int i = 0; i < n; i++) {
            c[i] = a[i].times(b[i]);
        }

        // compute inverse FFT
        return ifft(c);
    }


    // compute the linear convolution of x and y
    public static Complex[] convolve(Complex[] x, Complex[] y) {
        Complex ZERO = new Complex(0, 0);

        Complex[] a = new Complex[2*x.length];
        for (int i = 0;        i <   x.length; i++) a[i] = x[i];
        for (int i = x.length; i < 2*x.length; i++) a[i] = ZERO;

        Complex[] b = new Complex[2*y.length];
        for (int i = 0;        i <   y.length; i++) b[i] = y[i];
        for (int i = y.length; i < 2*y.length; i++) b[i] = ZERO;

        return cconvolve(a, b);
    }

    // preparation for visualization
    public static void prepare(Complex[] inputArray, int nSamplesPerSec) {
        int N = inputArray.length;
        int Nmax = (N + 1) / 2;
        int j = 0; // harmonic number
        double[] amplitude = new double[Nmax];
        double[] frequency = new double[Nmax];
        double limit = 0.01;
        double abs2min = Math.pow(limit, 2) * Math.pow(N, 2);
        double correctionCoefficient8Hz = 1.03;
        double correctionCoefficient20Hz = 2.17;

        amplitude8Hz = 0;
        amplitude20Hz = 0;

        if (inputArray[0].re() >= limit) {
            amplitude[j] = inputArray[0].re() / N;
            frequency[j] = 0;
            j++;
        }

        for (int i = 1; i < Nmax; i++) {
            double re = inputArray[i].re();
            double im = inputArray[i].im();

            double abs2 = Math.pow(re, 2) + Math.pow(im, 2);

            if (abs2 < abs2min) {
                continue;
            }

            amplitude[j] = 2.0 * Math.sqrt(abs2) / N;
            frequency[j] = nSamplesPerSec * i / N;


            if (frequency[j] == 8) {
                amplitude8Hz += amplitude[j] / correctionCoefficient8Hz;
            } else if (frequency[j] == 20) {
                amplitude20Hz += amplitude[j] * correctionCoefficient20Hz;
            }

            j++;
        }
        amplitude8Hz = (double)Math.round(amplitude8Hz * 10) / 10;
        amplitude20Hz = (double)Math.round(amplitude20Hz * 10) / 10;
    }
}
