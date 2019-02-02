package ru.avem.posum.utils;

public class RingBuffer {
    public double[] elements = null;

    public int capacity  = 0;
    public int writePos  = 0;
    public int available = 0;

    public RingBuffer(int capacity) {
        this.capacity = capacity;
        this.elements = new double[capacity];
    }

    public void reset() {
        this.writePos = 0;
        this.available = 0;
    }

    public boolean put(double[] element){

        if(available < capacity){
            if(writePos >= capacity){
                writePos = 0;
            }
            for (int i = 0; i < element.length; i++) {
                elements[writePos] = element[i];
                writePos++;
                available++;
            }
            return true;
        }

        return false;
    }

    public int take(double[] into, int length){
        int intoPos = 0;

        if(available <= writePos){
            int nextPos= writePos - available;
            int endPos   = nextPos + Math.min(available, length);

            for(;nextPos < endPos; nextPos++){
                into[intoPos++] = this.elements[nextPos];
            }
            this.available -= intoPos;
            return intoPos;
        } else {
            int nextPos = writePos - available + capacity;

            int leftInTop = capacity - nextPos;
            if(length <= leftInTop){
                //copy directly
                for(; intoPos < length; intoPos++){
                    into[intoPos] = this.elements[nextPos++];
                }
                this.available -= length;
                return length;

            } else {
                //copy top
                for(; nextPos < capacity; nextPos++){
                    into[intoPos++] = this.elements[nextPos];
                }

                //copy bottom - from 0 to writePos
                nextPos = 0;
                int leftToCopy = length - intoPos;
                int endPos = Math.min(writePos, leftToCopy);

                for(;nextPos < endPos; nextPos++){
                    into[intoPos++] = this.elements[nextPos];
                }

                this.available -= intoPos;

                return intoPos;
            }
        }
    }
}
