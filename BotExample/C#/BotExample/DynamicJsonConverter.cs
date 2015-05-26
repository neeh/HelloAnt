using System;
using System.Collections;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Dynamic;
using System.Linq;
using System.Text;
using System.Web.Script.Serialization;

// Class used to convert JSON to a C# object (without dependancy)
// See http://stackoverflow.com/questions/3142495/deserialize-json-into-c-sharp-dynamic-object
public sealed class DynamicJsonConverter : JavaScriptConverter
{
    public override object Deserialize(IDictionary<string, object> dictionary, Type type, JavaScriptSerializer serializer)
    {
        if (dictionary == null)
            throw new ArgumentNullException("dictionary");

        return type == typeof(object) ? new DynamicJsonObject(dictionary) : null;
    }

    public override IDictionary<string, object> Serialize(object obj, JavaScriptSerializer serializer)
    {
        throw new NotImplementedException();
    }

    public override IEnumerable<Type> SupportedTypes
    {
        get { return new ReadOnlyCollection<Type>(new List<Type>(new[] { typeof(object) })); }
    }

    #region Nested type: DynamicJsonObject

    private sealed class DynamicJsonObject : DynamicObject
    {
        private readonly IDictionary<string, object> _dictionary;

        public DynamicJsonObject(IDictionary<string, object> dictionary)
        {
            if (dictionary == null)
                throw new ArgumentNullException("dictionary");
            _dictionary = dictionary;
        }

        public override string ToString()
        {
            return ToString(_dictionary);
        }

        private string ToString(dynamic obj)
        {
            if (obj == null)
                return "null";
            if (obj is IDictionary<string, object>)
            {
                StringBuilder sb = new StringBuilder("{");
                bool first = true;
                foreach (var pair in obj)
                {
                    if (!first)
                        sb.Append(",");
                    else
                        first = false;
                    sb.AppendFormat("\"{0}\":{1}", pair.Key, ToString(pair.Value));
                }
                sb.Append("}");
                return sb.ToString();
            }
            if (obj is ArrayList)
            {
                StringBuilder sb = new StringBuilder("[");
                bool first = true;
                foreach (var arrayValue in (ArrayList)obj)
                {
                    if (!first)
                        sb.Append(",");
                    else
                        first = false;
                    sb.AppendFormat("{0}", ToString(arrayValue));

                }
                sb.Append("]");
                return sb.ToString();
            }
            if (obj is string)
                return "\"" + obj + "\"";
            return String.Format("{0}", obj);
        }

        public override bool TryGetMember(GetMemberBinder binder, out object result)
        {
            if (!_dictionary.TryGetValue(binder.Name, out result))
            {
                // return null to avoid exception.  caller can check for null this way...
                result = null;
                return true;
            }

            result = WrapResultObject(result);
            return true;
        }

        public override bool TryGetIndex(GetIndexBinder binder, object[] indexes, out object result)
        {
            if (indexes.Length == 1 && indexes[0] != null)
            {
                if (!_dictionary.TryGetValue(indexes[0].ToString(), out result))
                {
                    // return null to avoid exception.  caller can check for null this way...
                    result = null;
                    return true;
                }

                result = WrapResultObject(result);
                return true;
            }

            return base.TryGetIndex(binder, indexes, out result);
        }

        private static object WrapResultObject(object result)
        {
            var dictionary = result as IDictionary<string, object>;
            if (dictionary != null)
                return new DynamicJsonObject(dictionary);

            var arrayList = result as ArrayList;
            if (arrayList != null && arrayList.Count > 0)
            {
                return arrayList[0] is IDictionary<string, object>
                    ? new List<object>(arrayList.Cast<IDictionary<string, object>>().Select(x => new DynamicJsonObject(x)))
                    : new List<object>(arrayList.Cast<object>());
            }

            return result;
        }
    }

    #endregion
}