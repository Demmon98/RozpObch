using System;
using System.Diagnostics;
using System.Threading.Tasks;

class MultiplyMatrices
{
    static int[,] Fill(int rows, int cols)
    {
        int[,] matrix = new int[rows, cols];

        Random r = new Random();

        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                matrix[i, j] = r.Next(5);

        return matrix;
    }

    static void Multiply(int[,] a, int[,] b, int[,] c)
    {
        int aCols = a.GetLength(1);
        int bCols = b.GetLength(1);
        int aRows = a.GetLength(0);

        Parallel.For(0, aRows, i =>
        {
            for (int j = 0; j < bCols; j++)
            {
                int t = 0;

                for (int k = 0; k < aCols; k++)
                    t += a[i, k] * b[k, j];

                c[i, j] = t;
            }
        });
    }

    static void Main(string[] args)
    {
        int dim = 1000;

        int[,] A = Fill(dim, dim);
        int[,] B = Fill(dim, dim);
        int[,] C = new int[dim, dim];

        Stopwatch time = new Stopwatch();
        time.Start();
        Multiply(A, B, C);
        time.Stop();
        Console.WriteLine("DIMENSION [{0}x{0}]: {1} ms", dim, time.ElapsedMilliseconds);

        Console.ReadLine();
    }
}