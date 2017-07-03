package com.orisun.mining.util.hash;

import org.uncommons.maths.random.MersenneTwisterRNG;
import redis.clients.util.MurmurHash;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 
 * @Description: 最小函数。近似寻找相似项
 * @Author orisun
 * @Date 2016年7月6日
 */
public class MinHash {

    private static final long MAX_INT_SMALLER_TWIN_PRIME = 2147482949;
    private int functionNum;
    private List<IHash> hashFunctions;

    public MinHash(int functionNum, IHashGenerator hashGenerator) {
        this.functionNum = functionNum;
        this.hashFunctions = new ArrayList<IHash>(functionNum);
        for (int i = 0; i < functionNum; i++) {
            hashFunctions.add(hashGenerator.generate());
        }
    }

    public int[] getMinHash1(List<byte[]> bytesList) {
        int[] minHashValues = new int[functionNum];
        //hash值初始化为正无穷大
        for (int i = 0; i < functionNum; i++) {
            minHashValues[i] = Integer.MAX_VALUE;
        }
        for (int i = 0; i < functionNum; i++) {
            for (byte[] bytes : bytesList) {
                //计算hash值
                int hashValue = hashFunctions.get(i).hash(bytes);
                //只保留最小的那个hash
                if (hashValue < minHashValues[i]) {
                    minHashValues[i] = hashValue;
                }
            }
        }
        return minHashValues;
    }

    public int[] getMinHash2(List<Integer> intList) {
        List<byte[]> bytesList = new ArrayList<byte[]>();
        for (int value : intList) {
            byte[] bytes = new byte[4];
            bytes[0] = (byte) (value >> 24);
            bytes[1] = (byte) (value >> 16);
            bytes[2] = (byte) (value >> 8);
            bytes[3] = (byte) value;
            bytesList.add(bytes);
        }
        return getMinHash1(bytesList);
    }

    public int[] getMinHash3(String str) {
        List<byte[]> bytesList = new ArrayList<byte[]>();
        for (int i = 0; i < str.length(); i++) {
            byte[] bytes = new byte[1];
            bytes[0] = (byte) str.charAt(i);
            bytesList.add(bytes);
        }
        return getMinHash1(bytesList);
    }

    interface IHash {
        int hash(byte[] bytes);
    }

    static class LinearHash implements IHash {
        long seedA;
        long seedB;

        public LinearHash(long seedA, long seedB) {
            this.seedA = seedA;
            this.seedB = seedB;
        }

        @Override
        public int hash(byte[] bytes) {
            long hashValue = 31;
            for (long byteVal : bytes) {
                hashValue *= seedA * byteVal;
                hashValue += seedB;
            }
            return Math.abs((int) (hashValue % MAX_INT_SMALLER_TWIN_PRIME));
        }

    }

    static class PolynomialHash implements IHash {
        long seedA;
        long seedB;
        long seedC;

        public PolynomialHash(long seedA, long seedB, long seedC) {
            this.seedA = seedA;
            this.seedB = seedB;
            this.seedC = seedC;
        }

        @Override
        public int hash(byte[] bytes) {
            long hashValue = 31;
            for (long byteVal : bytes) {
                hashValue *= seedA * (byteVal >> 4);
                hashValue += seedB * byteVal + seedC;
            }
            return Math.abs((int) (hashValue % MAX_INT_SMALLER_TWIN_PRIME));
        }
    }

    static class MurmurHashWrapper implements IHash {
        int seed;

        public MurmurHashWrapper(int seed) {
            this.seed = seed;
        }

        @Override
        public int hash(byte[] bytes) {
            long hashValue = MurmurHash.hash64A(bytes, seed);
            return Math.abs((int) (hashValue % MAX_INT_SMALLER_TWIN_PRIME));
        }
    }

    static class MurmurHash3Wrapper implements IHash {
        int seed;

        public MurmurHash3Wrapper(int seed) {
            this.seed = seed;
        }

        @Override
        public int hash(byte[] bytes) {
            long hashValue = MurmurHash3.murmurhash3_x86_32(bytes, 0, bytes.length, seed);
            return Math.abs((int) (hashValue % MAX_INT_SMALLER_TWIN_PRIME));
        }
    }

    interface IHashGenerator {
        IHash generate();
    }

    public static class LinearHashGenerator implements IHashGenerator {
        private Random random = new MersenneTwisterRNG();

        @Override
        public IHash generate() {
            long seedA = random.nextLong();
            long seedB = random.nextLong();
            return new LinearHash(seedA, seedB);
        }
    }

    public static class PolynomialHashGenerator implements IHashGenerator {
        private Random random = new MersenneTwisterRNG();

        @Override
        public IHash generate() {
            long seedA = random.nextLong();
            long seedB = random.nextLong();
            long seedC = random.nextLong();
            return new PolynomialHash(seedA, seedB, seedC);
        }
    }

    public static class MurmurHashGenerator implements IHashGenerator {
        private Random random = new MersenneTwisterRNG();

        @Override
        public IHash generate() {
            int seed = random.nextInt();
            return new MurmurHashWrapper(seed);
        }
    }

    public static class MurmurHash3Generator implements IHashGenerator {
        private Random random = new MersenneTwisterRNG();

        @Override
        public IHash generate() {
            int seed = random.nextInt();
            return new MurmurHash3Wrapper(seed);
        }
    }

    public static class MultiHashGenerator implements IHashGenerator {
        private static long count = 0;

        @Override
        public IHash generate() {
            long mod = count++ % 4;
            if (mod == 0) {
                return new MurmurHashGenerator().generate();
            } else if (mod == 1) {
                return new MurmurHash3Generator().generate();
            } else if (mod == 2) {
                return new PolynomialHashGenerator().generate();
            } else {
                return new LinearHashGenerator().generate();
            }
        }
    }
}
