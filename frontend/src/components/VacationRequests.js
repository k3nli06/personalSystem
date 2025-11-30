import { useState, useEffect } from 'react';
import { supabase } from '../lib/supabase';
import { Calendar, Plus, Loader, Check, X, Clock } from 'lucide-react';

export default function VacationRequests({ userId, userRole }) {
  const [requests, setRequests] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    user_id: userId,
    start_date: '',
    end_date: '',
    reason: ''
  });

  useEffect(() => {
    fetchRequests();
    if (userRole === 'hr') {
      fetchEmployees();
    }
  }, [userId, userRole]);

  async function fetchRequests() {
    try {
      let query = supabase
        .from('vacation_requests')
        .select(`
          *,
          user:users!vacation_requests_user_id_fkey(full_name, email)
        `)
        .order('created_at', { ascending: false });

      if (userRole === 'employee') {
        query = query.eq('user_id', userId);
      }

      const { data, error } = await query;
      if (error) throw error;
      setRequests(data || []);
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

    const start = new Date(formData.start_date);
    const end = new Date(formData.end_date);
    const days = Math.ceil((end - start) / (1000 * 60 * 60 * 24)) + 1;

    try {
      const { error } = await supabase
        .from('vacation_requests')
        .insert({
          user_id: formData.user_id,
          start_date: formData.start_date,
          end_date: formData.end_date,
          total_days: days,
          reason: formData.reason,
          status: 'pending'
        });

      if (error) throw error;

      setShowForm(false);
      setFormData({ user_id: userId, start_date: '', end_date: '', reason: '' });
      fetchRequests();
    } catch (err) {
      console.error('Error:', err);
    }
  }

  async function handleStatusChange(requestId, newStatus) {
    try {
      const { error } = await supabase
        .from('vacation_requests')
        .update({
          status: newStatus,
          reviewed_by: userId,
          reviewed_at: new Date().toISOString()
        })
        .eq('id', requestId);

      if (error) throw error;
      fetchRequests();
    } catch (err) {
      console.error('Error:', err);
    }
  }

  const getStatusBadge = (status) => {
    const styles = {
      pending: 'bg-yellow-100 text-yellow-700',
      approved: 'bg-green-100 text-green-700',
      rejected: 'bg-red-100 text-red-700'
    };
    const labels = {
      pending: 'Pendiente',
      approved: 'Aprobada',
      rejected: 'Rechazada'
    };
    return (
      <span className={`px-3 py-1 rounded-full text-xs font-medium ${styles[status]}`}>
        {labels[status]}
      </span>
    );
  };

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
          {userRole === 'hr' ? 'Todas las Solicitudes' : 'Mis Solicitudes de Vacaciones'}
        </h3>
        |<button
          onClick={() => setShowForm(!showForm)}
          className="flex items-center gap-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors"
        >
          <Plus className="w-4 h-4" />
          Nueva Solicitud
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
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Fecha Inicio</label>
              <input
                type="date"
                value={formData.start_date}
                onChange={(e) => setFormData({ ...formData, start_date: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Fecha Fin</label>
              <input
                type="date"
                value={formData.end_date}
                onChange={(e) => setFormData({ ...formData, end_date: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                required
              />
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Motivo</label>
            <textarea
              value={formData.reason}
              onChange={(e) => setFormData({ ...formData, reason: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg"
              rows="3"
              required
            />
          </div>
          <div className="flex gap-2">
            <button type="submit" className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg">
              Enviar Solicitud
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
        {requests.map((request) => (
          <div key={request.id} className="bg-gray-50 p-4 rounded-lg border border-gray-200">
            <div className="flex items-start justify-between mb-3">
              <div className="flex items-start gap-3">
                <div className="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center">
                  <Calendar className="w-5 h-5 text-blue-600" />
                </div>
                <div>
                  {userRole === 'hr' && (
                    <p className="font-medium text-gray-900 mb-1">{request.user.full_name}</p>
                  )}
                  <p className="text-sm text-gray-600">
                    {new Date(request.start_date).toLocaleDateString('es-ES')} - {new Date(request.end_date).toLocaleDateString('es-ES')}
                  </p>
                  <p className="text-sm text-gray-500">{request.total_days} días</p>
                  <p className="text-sm text-gray-700 mt-2">{request.reason}</p>
                </div>
              </div>
              <div className="flex flex-col items-end gap-2">
                {getStatusBadge(request.status)}
                {userRole === 'hr' && request.status === 'pending' && (
                  <div className="flex gap-2 mt-2">
                    <button
                      onClick={() => handleStatusChange(request.id, 'approved')}
                      className="p-2 bg-green-100 hover:bg-green-200 text-green-700 rounded-lg transition-colors"
                      title="Aprobar"
                    >
                      <Check className="w-4 h-4" />
                    </button>
                    <button
                      onClick={() => handleStatusChange(request.id, 'rejected')}
                      className="p-2 bg-red-100 hover:bg-red-200 text-red-700 rounded-lg transition-colors"
                      title="Rechazar"
                    >
                      <X className="w-4 h-4" />
                    </button>
                  </div>
                )}
              </div>
            </div>
          </div>
        ))}
        {requests.length === 0 && (
          <p className="text-center text-gray-500 py-8">No hay solicitudes de vacaciones</p>
        )}
      </div>
    </div>
  );
}
