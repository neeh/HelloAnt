﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BotExample
{
    class Coordinates
    {
        public int Col { get; set; }
        public int Row { get; set; }

        public Coordinates(int Col, int Row)
        {
            this.Col = Col;
            this.Row = Row;
        }

        // Performs n % m (always >= 0)
        private int mod(int n, int m)
        {
            int val = n % m;
            return val < 0 ? val + m : val;
        }

        // Returns a new Coordinates corresponding to the current Coordinates with Col and Row within 0 and max (using modulo)
        public Coordinates Normalize(int maxCol, int maxRow)
        {
            return new Coordinates(mod(Col, maxCol), mod(Row, maxRow));
        }
        public Coordinates Normalize(GameMap map)
        {
            return Normalize(map.Cols, map.Rows);
        }

        // Returns a new Coordinates corresponding to the current Coordinates offseted by 1 in a specific direction
        public Coordinates ApplyDirection(AntDirection dir)
        {
            Coordinates val = new Coordinates(Col, Row);
            switch (dir)
            {
                case AntDirection.N: val.Row--;
                    break;
                case AntDirection.S: val.Row++;
                    break;
                case AntDirection.E: val.Col++;
                    break;
                case AntDirection.W: val.Col--;
                    break;
            }
            return val;
        }

        // Manhattan distance between two points
        public int Manhattan(Coordinates other)
        {
            return Math.Abs(Col - other.Col) + Math.Abs(Row - other.Row);
        }

        // Find the pair of value the closest to this one, using the Manhattan distance
        // (Not used, could be usefull to find the closest hill after collecting food)
        public Coordinates Closest(List<Coordinates> list)
        {
            int len = list.Count;
            if(len == 0)
                return null;
            Coordinates result = list[0];
            int min = Manhattan(result);
            for (int i = 1; i < len; i++)
            {
                Coordinates c = list[i];
                int dist = Manhattan(c);
                if (dist < min)
                {
                    result = c;
                    min = dist;
                }
            }
            return result;
        }

        // override object.Equals
        public override bool Equals(object obj)
        {
            var item = obj as Coordinates;
            if (item == null)
                return false;
            return Col.Equals(item.Col) && Row.Equals(item.Row);
        }

        // override object.GetHashCode
        public override int GetHashCode()
        {
            // http://www.cs.upc.edu/~alvarez/calculabilitat/enumerabilitat.pdf
            int tmp = Row + (Col + 1) / 2;
            return Col + (tmp * tmp);
            // return base.GetHashCode();
        }

        // override object.ToString
        public override string ToString()
        {
            return "{"+Col+";"+Row+"}";
        }
    }
}
