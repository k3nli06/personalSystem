import { useState, useEffect } from 'react';
import { supabase } from '../lib/supabase';
import { Clock, Plus, Loader, Calendar } from 'lucide-react';

export default function TimeRecords({ userId, userRole }) {
  const [records, setRecords] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    user_id: userId,
    date: new Date().toISOString().split('T')[0],
    check_in: '',
    check_out: '',
    notes: ''
  });

  useEffect(() => {
    fetchRecords();
    if (userRole === 'hr') {
      fetchEmployees();
    }
  }, [userId, userRole]);

  async function fetchRecords() {
    try {
      let query = supabase
        .from('time_records')
        .select('*, users(full_name, email)')
        .order('date', { ascending: false });

      if (userRole !== 'hr') {
        query = query.eq('user_id', userId);
      }
      const { data, error } = await query;

      if (error) throw error;
      setRecords(data || []);
    } catch (err) {
      console.error('Error:', err);
    } finally {
      setIsLoading(false);
    }
  }

  async function fetchEmployees() {
    try {
      const { data, error } = await supabase
        .from('users')
        .select('id, full_name, email')
        .order('full_name');
      if (error) throw error;
      setEmployees(data || []);
    } catch (err) {
      console.error('Error:', err);
    }
  }

  async function handleSubmit(e) {
    e.preventDefault();

    const checkIn = new Date(`${formData.date}T${formData.check_in}`);
    const checkOut = new Date(`${formData.date}T${formData.check_out}`);
    const hours = (checkOut - checkIn) / (1000 * 60 * 60);

    try {
      const { error } = await supabase
        .from('time_records')
        .insert({
          user_id: userId,
          date: formData.date,
          check_in: checkIn.toISOString(),
          check_out: checkOut.toISOString(),
          total_hours: hours.toFixed(2),
          notes: formData.notes
        });

      if (error) throw error;

      setShowForm(false);
      setFormData({ date: new Date().toISOString().split('T')[0], check_in: '', check_out: '', notes: '' });
      fetchRecords();
    } catch (err) {
      console.error('Error:', err);
    }
  }

  if (isLoading) {
    return (
      <div className="flex justify-center py-12">
        <Loader className="w-8 h-8 animate-spin text-blue-600" />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h3 className="text-lg font-semibold text-gray-900">
          {userRole === 'hr' ? 'Registros de Horas - Todos los Empleados' : 'Mis Registros de Horas'}
        </h3>
        <button
          onClick={() => setShowForm(!showForm)}
          className="flex items-center gap-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors"
        >
          <Plus className="w-4 h-4" />
          Nuevo Registro
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} className="bg-blue-50 p-6 rounded-lg space-y-4">
          {userRole === 'hr' && (
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Empleado</label>
              <select
                value={formData.user_id}
                onChange={(e) => setFormData({ ...formData, user_id: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                required
              >
                <option value="">Seleccionar empleado</option>
                {employees.map((emp) => (
                  <option key={emp.id} value={emp.id}>
                    {emp.full_name} ({emp.email})
                  </option>
                ))}
              </select>
            </div>
          )}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Fecha</label>
              <input
                type="date"
                value={formData.date}
                onChange={(e) => setFormData({ ...formData, date: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Hora Entrada</label>
              <input
                type="time"
                value={formData.check_in}
                onChange={(e) => setFormData({ ...formData, check_in: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Hora Salida</label>
              <input
                type="time"
                value={formData.check_out}
                onChange={(e) => setFormData({ ...formData, check_out: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                required
              />
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Notas (opcional)</label>
            <textarea
              value={formData.notes}
              onChange={(e) => setFormData({ ...formData, notes: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg"
              rows="2"
            />
          </div>
          <div className="flex gap-2">
            <button type="submit" className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg">
              Guardar
            </button>
            <button
              type="button"
              onClick={() => setShowForm(false)}
              className="px-4 py-2 bg-gray-200 hover:bg-gray-300 text-gray-700 rounded-lg"
            >
              Cancelar
            </button>
          </div>
        </form>
      )}

      <div className="space-y-3">
        {records.map((record) => (
          <div key={record.id} className="bg-gray-50 p-4 rounded-lg border border-gray-200">
            <div className="flex items-start justify-between">
              <div className="flex items-start gap-3">
                <div className="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center">
                  <Clock className="w-5 h-5 text-blue-600" />
                </div>
                <div>
                  {userRole === 'hr' && record.users && (
                    <p className="text-sm font-semibold text-blue-600 mb-1">
                      {record.users.full_name}
                    </p>
                  )}
                  <div className="flex items-center gap-2 mb-1">
                    <Calendar className="w-4 h-4 text-gray-500" />
                    <p className="font-medium text-gray-900">
                      {new Date(record.date).toLocaleDateString('es-ES', {
                        weekday: 'long',
                        year: 'numeric',
                        month: 'long',
                        day: 'numeric'
                      })}
                    </p>
                  </div>
                  <p className="text-sm text-gray-600">
                    Entrada: {new Date(record.check_in).toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' })}
                    {' • '}
                    Salida: {record.check_out ? new Date(record.check_out).toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' }) : 'Pendiente'}
                  </p>
                  {record.notes && (
                    <p className="text-sm text-gray-500 mt-1">{record.notes}</p>
                  )}
                </div>
              </div>
              <div className="text-right">
                <p className="text-lg font-bold text-blue-600">{record.total_hours || '0'} hrs</p>
              </div>
            </div>
          </div>
        ))}
        {records.length === 0 && (
          <p className="text-center text-gray-500 py-8">No hay registros de horas</p>
        )}
      </div>
    </div>
  );
}
