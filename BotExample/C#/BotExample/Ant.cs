using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BotExample
{
    class Ant
    {
        public bool HasFood { get; set; }
        public Stack<AntDirection> History { get; set; }
        public AntDirection CurrentMove { get; set; }

        public Ant()
        {
            HasFood = false;
            History = new Stack<AntDirection>();
            CurrentMove = AntDirection.U;
        }
    }

    enum AntDirection { U, N, S, E, W }
}
