﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BotExample
{
    class Ant
    {
        public bool HasFood { get; set; }
        // History of moves (used to backtrack)
        public Stack<AntDirection> History { get; set; }
        // Used to check every turn that the ant is still alive
        public bool Confirmed { get; set; }

        public Ant()
        {
            HasFood = false;
            History = new Stack<AntDirection>();
            Confirmed = true;
        }
    }

    // U = undefined
    enum AntDirection { U, N, S, E, W }
    static class AntDirectionMethods
    {
        public static AntDirection Reverse(this AntDirection dir)
        {
            switch (dir)
            {
                case AntDirection.N: return AntDirection.S;
                case AntDirection.S: return AntDirection.N;
                case AntDirection.E: return AntDirection.W;
                case AntDirection.W: return AntDirection.E;
            }
            return AntDirection.U;
        }
    }
}
